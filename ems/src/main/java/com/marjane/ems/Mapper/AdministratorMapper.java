package com.marjane.ems.Mapper;

import java.time.LocalDateTime;
import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Mapper class for Administrator (User with Role.ADMIN) to AdministratorResponse DTO.
 * @deprecated Use UserMapper instead
 */
@Deprecated
public class AdministratorMapper {

    /**
     * Converts a User entity (Administrator role) to an AdministratorResponse DTO.
     */
    public static AdministratorResponse toResponse(User admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Administrator cannot be null");
        }

        return new AdministratorResponse(
            admin.getId(),
            admin.getEid(),
            admin.getLastName(),
            admin.getFirstName(),
            admin.getEmail(),
            admin.getPhone(),
            admin.getRole() != null ? admin.getRole().name() : null,
            admin.getStatus() != null ? admin.getStatus().name() : null,
            admin.getCreatedAt(),
            admin.getUpdatedAt()
        );
    }

    /**
     * Converts an AdministratorRequest DTO to a User entity with ADMIN role.
     */
    public static User toEntity(AdministratorRequest request) {
        if (request == null) return null;

        User admin = new User();
        admin.setLastName(request.lastName());
        admin.setFirstName(request.firstName());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());

        return admin;
    }

    /**
     * Updates an existing User entity (Administrator) with data from AdministratorRequest DTO.
     */
    public static void updateEntity(User entity, AdministratorRequest request) {
        if (request == null || entity == null) return;

        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : UserStatus.ACTIVE);
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
