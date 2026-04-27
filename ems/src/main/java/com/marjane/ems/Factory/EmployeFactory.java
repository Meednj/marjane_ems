package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Factory class for creating Employee User entities.
 * Deprecated - use UserService instead.
 */
@Deprecated
public class EmployeFactory {

    private final PasswordEncoder passwordEncoder;

    public EmployeFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates an Employee User entity from an EmployeRequest.
     * @deprecated Use UserService.registerEmployee() instead
     */
    @Deprecated
    public User createEmploye(EmployeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("EmployeRequest cannot be null");
        }

        User employe = new User();
        employe.setLastName(request.lastName());
        employe.setFirstName(request.firstName());
        employe.setEmail(request.email());
        employe.setPhone(request.phone());
        employe.setPassword(passwordEncoder.encode(request.password()));
        employe.setRole(Role.EMPLOYEE);
        employe.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        employe.setDepartment(request.departement() != null ? request.departement() : "UNASSIGNED");
        employe.setCreatedAt(LocalDateTime.now());

        return employe;
    }
}
