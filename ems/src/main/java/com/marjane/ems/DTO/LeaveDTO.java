package com.marjane.ems.DTO;

import java.time.LocalDate;

import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Entities.LeaveType;

public record LeaveDTO(
    Long id,
    String EID,
    LocalDate startDate,
    LocalDate endDate,
    LeaveType type,
    String motif,
    LeaveStatus status,
    Long approverId,
    Long userId
) {}
