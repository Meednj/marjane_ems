package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


public record LeaveRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    Long approverId,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    @NotBlank(message = "Leave type is required")
    String type,

    String subject,

    String status
) {}
