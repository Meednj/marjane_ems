package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.Entities.Technician;

/**
 * Factory class for creating Technician entities.
 */
public class TechnicianFactory {

    private final PasswordEncoder passwordEncoder;

    public TechnicianFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a Technician entity from a TechnicianRequest.
     */
    public Technician createTechnician(TechnicianRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TechnicianRequest cannot be null");
        }

        Technician technician = new Technician();
        technician.setLastName(request.lastName());
        technician.setFirstName(request.firstName());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setPassword(passwordEncoder.encode(request.password()));
        technician.setRole("TECHNICIAN");
        technician.setStatus(request.status() != null ? request.status() : "ACTIVE");
        technician.setCreatedAt(LocalDateTime.now());

        return technician;
    }
}
