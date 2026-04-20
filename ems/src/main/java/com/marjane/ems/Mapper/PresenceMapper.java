package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.PresenceResponse;
import com.marjane.ems.Entities.Presence;

/**
 * Mapper class for Presence entity to PresenceResponse DTO.
 */
public class PresenceMapper {

    /**
     * Converts a Presence entity to a PresenceResponse DTO.
     */
    public static PresenceResponse toResponse(Presence presence) {
        if (presence == null) {
            throw new IllegalArgumentException("Presence cannot be null");
        }

        Long shiftId = presence.getShift() != null ? presence.getShift().getId() : null;

        return new PresenceResponse(
            presence.getId(),
            presence.getUser() != null ? SimpleUserMapper.toResponse(presence.getUser()) : null,
            presence.getPresenceDate(),
            presence.getArrivalTime(),
            presence.getDepartureTime(),
            presence.getStatus() != null ? presence.getStatus().name() : null,
            shiftId
        );
    }
}
