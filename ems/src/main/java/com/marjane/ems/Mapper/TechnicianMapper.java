package com.marjane.ems.Mapper;

import java.time.LocalDateTime;
import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Mapper class for Technician (User with Role.TECHNICIAN) to TechnicianResponse DTO.
 * @deprecated Use UserMapper instead
 */
@Deprecated
public class TechnicianMapper {

    /**
     * Converts a User entity (Technician role) to a TechnicianResponse DTO.
     */
    public static TechnicianResponse toResponse(User technician) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        return new TechnicianResponse(
            technician.getId(),
            technician.getEid(),
            technician.getLastName(),
            technician.getFirstName(),
            technician.getEmail(),
            technician.getPhone(),
            technician.getRole() != null ? technician.getRole().name() : null,
            technician.getStatus() != null ? technician.getStatus().name() : null,
            technician.getCreatedAt(),
            technician.getUpdatedAt()
        );
    }

    /**
     * Converts a TechnicianRequest DTO to a User entity with TECHNICIAN role.
     */
    public static User toEntity(TechnicianRequest request) {
        if (request == null) return null;

        User technician = new User();
        technician.setLastName(request.lastName());
        technician.setFirstName(request.firstName());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setStatus(request.status() != null ? 
            UserStatus.valueOf(request.status().toUpperCase()) : 
            UserStatus.ACTIVE);
        technician.setRole(Role.TECHNICIAN);
        technician.setCreatedAt(LocalDateTime.now());

        return technician;
    }

    /**
     * Updates an existing User entity (Technician) with data from TechnicianRequest DTO.
     */
    public static void updateEntity(User entity, TechnicianRequest request) {
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
