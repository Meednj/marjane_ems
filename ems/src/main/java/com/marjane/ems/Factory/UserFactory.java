package com.marjane.ems.Factory;

import com.marjane.ems.DTO.request.UserRequest;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Factory for creating User entities with appropriate role.
 * Refactored to work with single User entity and Role enum.
 */
public class UserFactory {

    /**
     * Create a user with the specified role.
     * @param request User request DTO
     * @param roleString Role as string (EMPLOYEE, TECHNICIAN, ADMIN)
     * @return Configured User entity
     */
    public User createUser(UserRequest request, String roleString) {
        User user = new User();
        
        Role role = parseRole(roleString);
        
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setRole(role);
        user.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        
        // Set role-specific fields
        if (role == Role.EMPLOYEE) {
            user.setDepartment("UNASSIGNED");
        }
        
        return user;
    }

    /**
     * Create a user with a Role enum.
     */
    public User createUser(UserRequest request, Role role) {
        User user = new User();
        
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setRole(role);
        user.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        
        // Set role-specific fields
        if (role == Role.EMPLOYEE) {
            user.setDepartment("UNASSIGNED");
        }
        
        return user;
    }

    /**
     * Parse role string to Role enum.
     */
    private Role parseRole(String roleString) {
        return switch (roleString.toUpperCase()) {
            case "EMPLOYE", "EMPLOYEE", "E" -> Role.EMPLOYEE;
            case "ADMINISTRATOR", "ADMIN", "A" -> Role.ADMIN;
            case "TECHNICIAN", "TECH", "T" -> Role.TECHNICIAN;
            default -> throw new IllegalArgumentException("Invalid role: " + roleString);
        };
    }
}