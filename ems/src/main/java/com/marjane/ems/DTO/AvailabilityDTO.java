package com.marjane.ems.DTO;

import java.time.LocalDateTime;

import com.marjane.ems.Entities.AvailabilityStatus;

public record AvailabilityDTO(
    Long id,
    Long userId,
    AvailabilityStatus status,
    LocalDateTime dateMiseAJour
) {}