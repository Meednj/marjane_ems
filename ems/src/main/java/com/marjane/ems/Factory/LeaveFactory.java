package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.Entities.Leave;
import com.marjane.ems.Entities.LeaveType;
import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Administrator;

/**
 * Factory class for creating Leave entities.
 */
public class LeaveFactory {

    /**
     * Creates a Leave entity from a LeaveRequest and User.
     */
    public Leave createLeave(LeaveRequest request, User user, Administrator approver) {
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
        
        // Generate EID if not present
        if (leave.getEID() == null) {
            leave.setEID("L" + System.currentTimeMillis());
        }

        return leave;
    }
}
