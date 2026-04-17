package com.marjane.ems.DTO.response;

import java.time.LocalDateTime;

/**
 * Response DTO for Availability entities.
 * Relationships represented as nested SimpleUserResponse to avoid circular references.
 */
public record AvailabilityResponse(
    Long id,
    SimpleUserResponse user,
    String status,
    LocalDateTime updateTime
) {}
