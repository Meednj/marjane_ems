package com.marjane.ems.Mapper;

import java.time.LocalDateTime;
import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Mapper class for Employee (User with Role.EMPLOYEE) to EmployeResponse DTO.
 * @deprecated Use UserMapper instead
 */
@Deprecated
public class EmployeMapper {

    /**
     * Converts a User entity (Employee role) to an EmployeResponse DTO.
     */
    public static EmployeResponse toResponse(User employe) {
        if (employe == null) {
            throw new IllegalArgumentException("Employe cannot be null");
        }

        return new EmployeResponse(
                employe.getId(),
                employe.getEid(),
                employe.getLastName(),
                employe.getFirstName(),
                employe.getEmail(),
                employe.getPhone(),
                employe.getRole() != null ? employe.getRole().name() : null,
                employe.getStatus() != null ? employe.getStatus().name() : null,
                employe.getDepartment(),
                employe.getCreatedAt(),
                employe.getUpdatedAt());
    }
    
    public static User toEntity(EmployeRequest request) {
        if (request == null) return null;

        User employe = new User();
        // Base User fields
        employe.setLastName(request.lastName());
        employe.setFirstName(request.firstName());
        employe.setEmail(request.email());
        employe.setPhone(request.phone());
        employe.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        employe.setRole(Role.EMPLOYEE);

        employe.setDepartment(request.departement());
        employe.setCreatedAt(LocalDateTime.now());
        
        return employe;
    }

    public static void updateEntity(User entity, EmployeRequest request) {
        if (request == null || entity == null) return;

        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : UserStatus.ACTIVE);
        entity.setDepartment(request.departement());
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
