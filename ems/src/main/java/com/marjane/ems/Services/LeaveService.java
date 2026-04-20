package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.DTO.response.LeaveResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveService {
    LeaveResponse createLeave(LeaveRequest request);
    Optional<LeaveResponse> getLeaveById(Long id);
    List<LeaveResponse> getAllLeaves();
    List<LeaveResponse> getLeavesByUser(Long userId);
    List<LeaveResponse> getLeavesByStatus(String status);
    List<LeaveResponse> getLeavesByType(String type);
    List<LeaveResponse> getLeavesByDateRange(LocalDate startDate, LocalDate endDate);
    List<LeaveResponse> getPendingLeaveRequests();
    List<LeaveResponse> getLeavesByApprover(Long approverId);
    LeaveResponse updateLeave(Long id, LeaveRequest request);
    LeaveResponse approveLeave(Long leaveId, Long approverId);
    LeaveResponse rejectLeave(Long leaveId, Long approverId);
    void deleteLeave(Long id);
}
