package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;


public record LeaveRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    Long approverId,

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    LocalDate endDate,

    @NotBlank(message = "Leave type is required")
    String type,

    String subject,

    String status
) {}
