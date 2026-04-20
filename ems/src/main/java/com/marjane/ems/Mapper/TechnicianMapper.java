package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.Technician;

/**
 * Mapper class for Technician entity to TechnicianResponse DTO.
 */
public class TechnicianMapper {

    /**
     * Converts a Technician entity to a TechnicianResponse DTO.
     */
    public static TechnicianResponse toResponse(Technician technician) {
        if (technician == null) {
            throw new IllegalArgumentException("Technician cannot be null");
        }

        return new TechnicianResponse(
            technician.getId(),
            technician.getEID(),
            technician.getLastName(),
            technician.getFirstName(),
            technician.getEmail(),
            technician.getPhone(),
            technician.getRole(),
            technician.getStatus(),
            technician.getCreatedAt(),
            technician.getUpdatedAt()
        );
    }
}
