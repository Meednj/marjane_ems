package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.PresenceRequest;
import com.marjane.ems.DTO.response.PresenceResponse;
import com.marjane.ems.Entities.Presence;
import com.marjane.ems.Entities.PresenceStatus;

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

    /**
     * Converts a PresenceRequest DTO to a Presence entity.
     */
    public static Presence toEntity(PresenceRequest request) {
        if (request == null) return null;

        Presence presence = new Presence();
        presence.setPresenceDate(request.presenceDate());
        presence.setArrivalTime(request.arrivalTime());
        presence.setDepartureTime(request.departureTime());
        presence.setStatus(request.status() != null ? 
            PresenceStatus.valueOf(request.status()) : null);

        return presence;
    }

    /**
     * Updates an existing Presence entity with data from PresenceRequest DTO.
     */
    public static void updateEntity(Presence entity, PresenceRequest request) {
        if (request == null || entity == null) return;

        entity.setPresenceDate(request.presenceDate());
        entity.setArrivalTime(request.arrivalTime());
        entity.setDepartureTime(request.departureTime());
        entity.setStatus(request.status() != null ? 
            PresenceStatus.valueOf(request.status()) : null);
    }
}
