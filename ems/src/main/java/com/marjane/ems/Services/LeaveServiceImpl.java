package com.marjane.ems.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.LeaveRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.DTO.response.LeaveResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.Leave;
import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Mapper.LeaveMapper;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserRepository userRepository;

    public LeaveServiceImpl(LeaveRepository leaveRepository, UserRepository userRepository) {
        this.leaveRepository = leaveRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LeaveResponse createLeave(LeaveRequest request) {
        Leave leave = LeaveMapper.toEntity(request);
        
        userRepository.findById(request.userId())
            .ifPresentOrElse(
                user -> leave.setUser(user),
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

        if (leaves.isEmpty()) {
            throw new RuntimeException("No leaves found for user ID: " + userId);
        }

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByStatus(String status) {
        List<Leave> leaves = leaveRepository.findByStatusIgnoreCase(status);

        if (leaves.isEmpty()) {
            throw new RuntimeException("No leaves found with status: " + status);
        }

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByType(String type) {
        List<Leave> leaves = leaveRepository.findByTypeIgnoreCase(type);

        if (leaves.isEmpty()) {
            throw new RuntimeException("No leaves found with type: " + type);
        }

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Leave> leaves = leaveRepository.findByStartDateBetween(startDate, endDate);

        if (leaves.isEmpty()) {
            throw new RuntimeException("No leaves found in the date range");
        }

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public List<LeaveResponse> getPendingLeaveRequests() {
        return getLeavesByStatus(LeaveStatus.PENDING.name());
    }

    @Override
    public List<LeaveResponse> getLeavesByApprover(Long approverId) {
        List<Leave> leaves = leaveRepository.findByApproverId(approverId);

        if (leaves.isEmpty()) {
            throw new RuntimeException("No leaves found for approver ID: " + approverId);
        }

        return leaves.stream().map(LeaveMapper::toResponse).toList();
    }

    @Override
    public LeaveResponse updateLeave(Long id, LeaveRequest request) {
        Leave leave = leaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + id));

        LeaveMapper.updateEntity(leave, request);
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
    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new RuntimeException("Leave not found with ID: " + id);
        }
        leaveRepository.deleteById(id);
    }
}
