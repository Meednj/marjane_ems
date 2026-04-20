package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.Entities.Employe;

/**
 * Factory class for creating Employe entities.
 */
public class EmployeFactory {

    private final PasswordEncoder passwordEncoder;

    public EmployeFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates an Employe entity from an EmployeRequest.
     */
    public Employe createEmploye(EmployeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("EmployeRequest cannot be null");
        }

        Employe employe = new Employe();
        employe.setLastName(request.lastName());
        employe.setFirstName(request.firstName());
        employe.setEmail(request.email());
        employe.setPhone(request.phone());
        employe.setPassword(passwordEncoder.encode(request.password()));
        employe.setRole("EMPLOYE");
        employe.setStatus(request.status() != null ? request.status() : "ACTIVE");
        employe.setDepartement(request.departement());
        employe.setActivityStatus(request.activityStatus() != null ? request.activityStatus() : "ACTIVE");
        employe.setCreatedAt(LocalDateTime.now());

        return employe;
    }
}
