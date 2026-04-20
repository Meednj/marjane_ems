package com.marjane.ems.Mapper;

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
}
