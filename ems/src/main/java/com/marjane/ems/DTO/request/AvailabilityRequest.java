package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record AvailabilityRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotBlank(message = "Status is required")
    String status
) {}