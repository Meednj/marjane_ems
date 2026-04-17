package com.marjane.ems.DTO.response;

import java.time.LocalDate;

/**
 * Response DTO for Leave entities.
 * Relationships represented using nested SimpleUserResponse to avoid circular references.
 */
public record LeaveResponse(
    Long id,
    String EID,
    SimpleUserResponse user,
    SimpleUserResponse approver,
    LocalDate startDate,
    LocalDate endDate,
    String type,
    String subject,
    String status
) {}
