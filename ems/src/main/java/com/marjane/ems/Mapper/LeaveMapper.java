package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.LeaveResponse;
import com.marjane.ems.Entities.Leave;

/**
 * Mapper class for Leave entity to LeaveResponse DTO.
 */
public class LeaveMapper {

    /**
     * Converts a Leave entity to a LeaveResponse DTO.
     */
    public static LeaveResponse toResponse(Leave leave) {
        if (leave == null) {
            throw new IllegalArgumentException("Leave cannot be null");
        }

        return new LeaveResponse(
            leave.getId(),
            leave.getEID(),
            leave.getUser() != null ? SimpleUserMapper.toResponse(leave.getUser()) : null,
            leave.getApprover() != null ? SimpleUserMapper.toResponse(leave.getApprover()) : null,
            leave.getStartDate(),
            leave.getEndDate(),
            leave.getType() != null ? leave.getType().name() : null,
            leave.getSubject(),
            leave.getStatus() != null ? leave.getStatus().name() : null
        );
    }
}
