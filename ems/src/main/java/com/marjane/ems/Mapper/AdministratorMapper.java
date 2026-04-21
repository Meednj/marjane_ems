package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import com.marjane.ems.Entities.Administrator;

/**
 * Mapper class for Administrator entity to AdministratorResponse DTO.
 */
public class AdministratorMapper {

    /**
     * Converts an Administrator entity to an AdministratorResponse DTO.
     */
    public static AdministratorResponse toResponse(Administrator administrator) {
        if (administrator == null) {
            throw new IllegalArgumentException("Administrator cannot be null");
        }

        return new AdministratorResponse(
            administrator.getId(),
            administrator.getEID(),
            administrator.getLastName(),
            administrator.getFirstName(),
            administrator.getEmail(),
            administrator.getPhone(),
            administrator.getRole(),
            administrator.getStatus(),
            administrator.getCreatedAt(),
            administrator.getUpdatedAt()
        );
    }

    /**
     * Converts an AdministratorRequest DTO to an Administrator entity.
     */
    public static Administrator toEntity(AdministratorRequest request) {
        if (request == null) return null;

        Administrator administrator = new Administrator();
        administrator.setLastName(request.lastName());
        administrator.setFirstName(request.firstName());
        administrator.setEmail(request.email());
        administrator.setPhone(request.phone());
        administrator.setStatus(request.status());

        return administrator;
    }

    /**
     * Updates an existing Administrator entity with data from AdministratorRequest DTO.
     */
    public static void updateEntity(Administrator entity, AdministratorRequest request) {
        if (request == null || entity == null) return;

        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status());
    }
}
