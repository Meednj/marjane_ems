package com.marjane.ems.DTO;

import java.time.LocalDateTime;

import com.marjane.ems.Entities.TicketActions;

public record TicketHistoryDTO(
    Long id,
    Long ticketId,
    String oldStatus,
    String newStatus,
    TicketActions action,
    LocalDateTime actionDate,
    Long executorId
) {}