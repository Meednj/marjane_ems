package com.marjane.ems.DTO.response;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO for Presence entities.
 * Relationships represented as IDs or nested SimpleUserResponse to avoid circular references.
 */
public record PresenceResponse(
    Long id,
    SimpleUserResponse user,
    LocalDate presenceDate,
    LocalTime arrivalTime,
    LocalTime departureTime,
    String status,
    Long shiftId
) {}
