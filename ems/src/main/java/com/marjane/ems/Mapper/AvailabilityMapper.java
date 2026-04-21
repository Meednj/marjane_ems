package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.AvailabilityRequest;
import com.marjane.ems.DTO.response.AvailabilityResponse;
import com.marjane.ems.Entities.Availability;
import com.marjane.ems.Entities.AvailabilityStatus;

/**
 * Mapper class for Availability entity to AvailabilityResponse DTO.
 */
public class AvailabilityMapper {

    /**
     * Converts an Availability entity to an AvailabilityResponse DTO.
     */
    public static AvailabilityResponse toResponse(Availability availability) {
        if (availability == null) {
            throw new IllegalArgumentException("Availability cannot be null");
        }

        return new AvailabilityResponse(
            availability.getId(),
            availability.getUser() != null ? SimpleUserMapper.toResponse(availability.getUser()) : null,
            availability.getStatus() != null ? availability.getStatus().name() : null,
            availability.getUpdateTime()
        );
    }

    /**
     * Converts an AvailabilityRequest DTO to an Availability entity.
     */
    public static Availability toEntity(AvailabilityRequest request) {
        if (request == null) return null;

        Availability availability = new Availability();
        availability.setStatus(request.status() != null ? 
            AvailabilityStatus.valueOf(request.status()) : null);

        return availability;
    }

    /**
     * Updates an existing Availability entity with data from AvailabilityRequest DTO.
     */
    public static void updateEntity(Availability entity, AvailabilityRequest request) {
        if (request == null || entity == null) return;

        entity.setStatus(request.status() != null ? 
            AvailabilityStatus.valueOf(request.status()) : null);
    }
}
