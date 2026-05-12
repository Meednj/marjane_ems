package com.marjane.ems.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.marjane.ems.DAL.AvailabilityRepository;
import com.marjane.ems.DAL.LeaveRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.DTO.response.LeaveResponse;
import com.marjane.ems.Entities.Availability;
import com.marjane.ems.Entities.AvailabilityStatus;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.Leave;
import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Entities.LeaveType;
import com.marjane.ems.Entities.UserStatus;
import com.marjane.ems.Mapper.LeaveMapper;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;

    public LeaveServiceImpl(LeaveRepository leaveRepository,
            UserRepository userRepository,
            AvailabilityRepository availabilityRepository) {
        this.leaveRepository = leaveRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public LeaveResponse createLeave(LeaveRequest request) {
        Leave leave = LeaveMapper.toEntity(request);
        // Validate dates: startDate must not be in the past and endDate must be after or equal to startDate
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        if (leave.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        userRepository.findById(request.userId())
            .ifPresentOrElse(
                user -> {
                    leave.setUser(user);
                    // Prevent creating more than one leave for the same user
                    var existing = leaveRepository.findByUserId(user.getId());
                    if (existing != null && !existing.isEmpty()) {
                        throw new IllegalArgumentException("A leave already exists for this user. Only one leave is allowed per user.");
                    }
                    // Check for overlapping leaves for this user (redundant now, kept for safety)
                    if (leave.getStartDate() != null && leave.getEndDate() != null) {
                        var overlaps = leaveRepository.findOverlappingLeaves(user.getId(), leave.getStartDate(), leave.getEndDate());
                        if (!overlaps.isEmpty()) {
                            throw new IllegalArgumentException("Overlapping leave request already exists for this user and date range");
                        }
                    }
                },
                () -> {
                    throw new RuntimeException("User not found with ID: " + request.userId());
                }
            );

        if (request.approverId() != null) {
            userRepository.findById(request.approverId())
                .filter(user -> user.getRole() == Role.ADMIN)
                .ifPresentOrElse(
                    user -> leave.setApprover(user),
                    () -> {
                        throw new RuntimeException("Approver not found or not an Administrator with ID: " + request.approverId());
                    }
                );
        }

        leave.setStatus(LeaveStatus.PENDING);
        return LeaveMapper.toResponse(leaveRepository.save(leave));
    }

    @Override
    public Optional<LeaveResponse> getLeaveById(Long id) {
        return leaveRepository.findById(id).map(LeaveMapper::toResponse);
    }

    @Override
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAll().stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByUser(Long userId) {
        List<Leave> leaves = leaveRepository.findByUserId(userId);

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByStatus(String status) {
        List<Leave> leaves = leaveRepository.findByStatus(LeaveStatus.valueOf(status.toUpperCase()));

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByType(String type) {
        List<Leave> leaves = leaveRepository.findByType(LeaveType.valueOf(type.toUpperCase()));

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Leave> leaves = leaveRepository.findByStartDateBetween(startDate, endDate);

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getPendingLeaveRequests() {
        return getLeavesByStatus(LeaveStatus.PENDING.name());
    }

    @Override
    public List<LeaveResponse> getLeavesByApprover(Long approverId) {
        List<Leave> leaves = leaveRepository.findByApproverId(approverId);

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public LeaveResponse updateLeave(Long id, LeaveRequest request) {
        Leave leave = leaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + id));

        // Apply updates then validate dates
        LeaveMapper.updateEntity(leave, request);
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        if (leave.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        // Check for overlapping leaves excluding current leave id
        if (leave.getUser() != null) {
            var overlaps = leaveRepository.findOverlappingLeavesExcludingId(leave.getUser().getId(), leave.getId(), leave.getStartDate(), leave.getEndDate());
            if (!overlaps.isEmpty()) {
                throw new IllegalArgumentException("An overlapping leave already exists for this user and date range");
            }
        }

        return LeaveMapper.toResponse(leaveRepository.save(leave));
    }

    @Override
    public LeaveResponse approveLeave(Long leaveId, Long approverId) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + leaveId));

        User approver = userRepository.findById(approverId)
            .filter(user -> user.getRole() == Role.ADMIN)
            .orElseThrow(() -> new RuntimeException("Approver not found or not an Administrator with ID: " + approverId));

        leave.setApprover(approver);
        leave.setStatus(LeaveStatus.APPROVED);

        if (leave.getUser() != null) {
            User user = leave.getUser();
            user.setStatus(UserStatus.ON_LEAVE);
            userRepository.save(user);

            Availability availability = availabilityRepository.findByUserId(leave.getUser().getId())
                .orElseGet(() -> {
                    Availability newAvailability = new Availability();
                    newAvailability.setUser(leave.getUser());
                    return newAvailability;
                });
            availability.setStatus(AvailabilityStatus.UNAVAILABLE);
            availabilityRepository.save(availability);
        }

        return LeaveMapper.toResponse(leaveRepository.save(leave));
    }

    @Override
    public LeaveResponse rejectLeave(Long leaveId, Long approverId) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + leaveId));

        User approver = userRepository.findById(approverId)
            .filter(user -> user.getRole() == Role.ADMIN)
            .orElseThrow(() -> new RuntimeException("Approver not found or not an Administrator with ID: " + approverId));

        leave.setApprover(approver);
        leave.setStatus(LeaveStatus.REJECTED);
        return LeaveMapper.toResponse(leaveRepository.save(leave));
    }

    @Override
    @Transactional
    public void deleteLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + id));

        if (leave.getUser() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEid = authentication != null ? authentication.getName() : null;
            boolean isLeaveOwnerConnected = currentEid != null && currentEid.equalsIgnoreCase(leave.getUser().getEid());
            UserStatus restoredStatus = isLeaveOwnerConnected ? UserStatus.ACTIVE : UserStatus.INACTIVE;

            User user = leave.getUser();
            user.setStatus(restoredStatus);
            userRepository.save(user);

            Availability availability = availabilityRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Availability newAvailability = new Availability();
                    newAvailability.setUser(user);
                    return newAvailability;
                });
            availability.setStatus(isLeaveOwnerConnected ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.UNAVAILABLE);
            availabilityRepository.save(availability);
        }

        leaveRepository.deleteById(id);
    }
}
