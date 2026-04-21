package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.TechnicianRequest;
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

    /**
     * Converts a TechnicianRequest DTO to a Technician entity.
     */
    public static Technician toEntity(TechnicianRequest request) {
        if (request == null) return null;

        Technician technician = new Technician();
        technician.setLastName(request.lastName());
        technician.setFirstName(request.firstName());
        technician.setEmail(request.email());
        technician.setPhone(request.phone());
        technician.setStatus(request.status());

        return technician;
    }

    /**
     * Updates an existing Technician entity with data from TechnicianRequest DTO.
     */
    public static void updateEntity(Technician entity, TechnicianRequest request) {
        if (request == null || entity == null) return;

        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status());
    }
}
