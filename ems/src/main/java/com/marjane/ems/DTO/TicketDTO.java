package com.marjane.ems.DTO;

import java.time.LocalDateTime;

import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;

public record TicketDTO(
    Long id,
    Long createurId,      // References Employe
    Long technicianId,    // References Technician
    String title,
    String description,
    TicketCategory category,
    TicketPriority priority,
    TicketStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime resolvedAt
) {}