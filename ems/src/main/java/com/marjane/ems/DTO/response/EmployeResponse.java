package com.marjane.ems.DTO.response;

import java.time.LocalDateTime;

/**
 * Response DTO for Employe entities.
 * Includes generated fields, auditing timestamps, and employee-specific attributes.
 * Excludes sensitive fields like password.
 * Relationships represented by IDs to avoid deep nesting.
 */
public record EmployeResponse(
    Long id,
    String EID,
    String lastName,
    String firstName,
    String email,
    String phone,
    String role,
    String status,
    String departement,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
