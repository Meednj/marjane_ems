package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.Entities.Administrator;

/**
 * Factory class for creating Administrator entities.
 */
public class AdministratorFactory {

    private final PasswordEncoder passwordEncoder;

    public AdministratorFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates an Administrator entity from an AdministratorRequest.
     */
    public Administrator createAdministrator(AdministratorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AdministratorRequest cannot be null");
        }

        Administrator admin = new Administrator();
        admin.setLastName(request.lastName());
        admin.setFirstName(request.firstName());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole("ADMINISTRATOR");
        admin.setStatus(request.status() != null ? request.status() : "ACTIVE");
        admin.setCreatedAt(LocalDateTime.now());

        return admin;
    }
}
