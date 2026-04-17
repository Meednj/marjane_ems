package com.marjane.ems.DTO.response;

import java.time.LocalDateTime;

/**
 * Response DTO for User entities.
 * Includes generated fields and auditing timestamps.
 * Excludes sensitive fields like password.
 */
public record UserResponse(
    Long id,
    String EID,
    String lastName,
    String firstName,
    String email,
    String phone,
    String role,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
