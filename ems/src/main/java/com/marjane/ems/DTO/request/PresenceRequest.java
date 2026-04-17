package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;


public record PresenceRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotNull(message = "Presence date is required")
    LocalDate presenceDate,

    LocalTime arrivalTime,

    LocalTime departureTime,

    String status,

    Long shiftId
) {}
