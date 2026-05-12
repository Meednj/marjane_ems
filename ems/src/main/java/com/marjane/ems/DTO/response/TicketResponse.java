package com.marjane.ems.DTO.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Ticket entities.
 * Includes generated fields and auditing timestamps.
 * Relationships represented using nested SimpleUserResponse to avoid circular references.
 */
public record TicketResponse(
    Long id,
    SimpleUserResponse creator,
    List<SimpleUserResponse> technicians,
    String title,
    String description,
    String category,
    String priority,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime resolvedAt
) {}
