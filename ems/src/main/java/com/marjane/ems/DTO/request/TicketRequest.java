package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record TicketRequest(
    @NotNull(message = "Creator ID is required")
    Long creatorId,

    Long technicianId,

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    @NotBlank(message = "Category is required")
    String category,

    @NotBlank(message = "Priority is required")
    String priority,

    String status
) {}
