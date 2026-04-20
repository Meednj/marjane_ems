package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import com.marjane.ems.DTO.request.PresenceRequest;
import com.marjane.ems.Entities.Presence;
import com.marjane.ems.Entities.PresenceStatus;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Shift;

/**
 * Factory class for creating Presence entities.
 */
public class PresenceFactory {

    /**
     * Creates a Presence entity from a PresenceRequest and User.
     */
    public Presence createPresence(PresenceRequest request, User user, Shift shift) {
        if (request == null) {
            throw new IllegalArgumentException("PresenceRequest cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Presence presence = new Presence();
        presence.setUser(user);
        presence.setPresenceDate(request.presenceDate());
        presence.setArrivalTime(request.arrivalTime());
        presence.setDepartureTime(request.departureTime());
        presence.setStatus(request.status() != null ? 
            PresenceStatus.valueOf(request.status().toUpperCase()) : 
            PresenceStatus.PRESENT);
        presence.setShift(shift);

        return presence;
    }
}
