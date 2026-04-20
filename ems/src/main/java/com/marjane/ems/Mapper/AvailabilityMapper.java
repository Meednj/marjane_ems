package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.AvailabilityResponse;
import com.marjane.ems.Entities.Availability;

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
}
