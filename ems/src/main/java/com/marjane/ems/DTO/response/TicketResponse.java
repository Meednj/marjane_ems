package com.marjane.ems.DTO.response;

import java.time.LocalDateTime;

/**
 * Response DTO for Ticket entities.
 * Includes generated fields and auditing timestamps.
 * Relationships represented using nested SimpleUserResponse to avoid circular references.
 */
public record TicketResponse(
    Long id,
    SimpleUserResponse creator,
    SimpleUserResponse technician,
    String title,
    String description,
    String category,
    String priority,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime resolvedAt
) {}
