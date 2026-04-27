package com.marjane.ems.Factory;

import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.Entities.Leave;
import com.marjane.ems.Entities.LeaveType;
import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Entities.User;
import java.time.LocalDateTime;

/**
 * Factory class for creating Leave entities.
 * @deprecated Use LeaveService instead
 */
@Deprecated
public class LeaveFactory {

    /**
     * Creates a Leave entity from a LeaveRequest and User.
     */
    public Leave createLeave(LeaveRequest request, User user, User approver) {
        if (request == null) {
            throw new IllegalArgumentException("LeaveRequest cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Leave leave = new Leave();
        leave.setUser(user);
        leave.setApprover(approver);
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setType(LeaveType.valueOf(request.type().toUpperCase()));
        leave.setSubject(request.subject());
        leave.setStatus(request.status() != null ? 
            LeaveStatus.valueOf(request.status().toUpperCase()) : 
            LeaveStatus.PENDING);
        leave.setCreatedAt(LocalDateTime.now());

        return leave;
    }
}
