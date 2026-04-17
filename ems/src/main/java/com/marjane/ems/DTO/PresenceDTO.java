package com.marjane.ems.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import com.marjane.ems.Entities.PresenceStatus;


public record PresenceDTO(
    Long id,
    Long userId,
    LocalDate datePresence,
    LocalTime heureArrivee,
    LocalTime heureDepart,
    PresenceStatus status,
    Long shiftId
) {}
