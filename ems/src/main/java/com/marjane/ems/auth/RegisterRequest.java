package com.marjane.ems.auth;

import com.marjane.ems.Entities.Role;

/**
 * Registration request DTO.
 */
public record RegisterRequest(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
    Role role
) {}
