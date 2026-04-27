package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Factory class for creating Technician User entities.
 * Deprecated - use UserService instead.
 */
public class TechnicianFactory {

    private final PasswordEncoder passwordEncoder;

    public TechnicianFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a Technician User entity from a TechnicianRequest.
     * @deprecated Use UserService.registerUser() instead
     */
    @Deprecated
    public User createTechnician(TechnicianRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TechnicianRequest cannot be null");
        }

        User technician = new User();
        technician.setLastName(request.lastName());
        technician.setFirstName(request.firstName());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setPassword(passwordEncoder.encode(request.password()));
        technician.setRole(Role.TECHNICIAN);
        technician.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        technician.setCreatedAt(LocalDateTime.now());

        return technician;
    }
}
