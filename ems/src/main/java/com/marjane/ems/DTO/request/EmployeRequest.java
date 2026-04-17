package com.marjane.ems.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record EmployeRequest(
    @NotBlank(message = "Last name is required")
    String lastName,

    @NotBlank(message = "First name is required")
    String firstName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Phone is required")
    String phone,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    @NotBlank(message = "Department is required")
    String departement,

    String activityStatus,

    String status
) {}
