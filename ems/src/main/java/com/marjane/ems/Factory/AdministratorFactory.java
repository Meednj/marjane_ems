package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Factory class for creating Administrator User entities.
 * Deprecated - use UserService instead.
 */
public class AdministratorFactory {

    private final PasswordEncoder passwordEncoder;

    public AdministratorFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates an Administrator User entity from an AdministratorRequest.
     * @deprecated Use UserService.registerUser() instead
     */
    @Deprecated
    public User createAdministrator(AdministratorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AdministratorRequest cannot be null");
        }

        User admin = new User();
        admin.setLastName(request.lastName());
        admin.setFirstName(request.firstName());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(Role.ADMIN);
        admin.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        admin.setCreatedAt(LocalDateTime.now());

        return admin;
    }
}
