package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import com.marjane.ems.DTO.request.AvailabilityRequest;
import com.marjane.ems.Entities.Availability;
import com.marjane.ems.Entities.AvailabilityStatus;
import com.marjane.ems.Entities.User;

/**
 * Factory class for creating Availability entities.
 */
public class AvailabilityFactory {

    /**
     * Creates an Availability entity from an AvailabilityRequest and User.
     */
    public Availability createAvailability(AvailabilityRequest request, User user) {
        if (request == null) {
            throw new IllegalArgumentException("AvailabilityRequest cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Availability availability = new Availability();
        availability.setUser(user);
        availability.setStatus(AvailabilityStatus.valueOf(request.status().toUpperCase()));
        availability.setUpdateTime(LocalDateTime.now());

        return availability;
    }
}
