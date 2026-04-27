package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.LeaveRequest;
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
            null,  // Leave doesn't have an EID field
            leave.getUser() != null ? SimpleUserMapper.toResponse(leave.getUser()) : null,
            leave.getApprover() != null ? SimpleUserMapper.toResponse(leave.getApprover()) : null,
            leave.getStartDate(),
            leave.getEndDate(),
            leave.getType() != null ? leave.getType().name() : null,
            leave.getSubject(),
            leave.getStatus() != null ? leave.getStatus().name() : null
        );
    }

    /**
     * Converts a LeaveRequest DTO to a Leave entity.
     */
    public static Leave toEntity(LeaveRequest request) {
        if (request == null) return null;

        Leave leave = new Leave();
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setSubject(request.subject());

        return leave;
    }

    /**
     * Updates an existing Leave entity with data from LeaveRequest DTO.
     */
    public static void updateEntity(Leave entity, LeaveRequest request) {
        if (request == null || entity == null) return;

        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());
        entity.setSubject(request.subject());
    }
}
