package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.TicketResponse;
import com.marjane.ems.Entities.Ticket;

/**
 * Mapper class for Ticket entity to TicketResponse DTO.
 */
public class TicketMapper {

    /**
     * Converts a Ticket entity to a TicketResponse DTO.
     */
    public static TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be null");
        }

        return new TicketResponse(
            ticket.getId(),
            ticket.getCreator() != null ? SimpleUserMapper.toResponse(ticket.getCreator()) : null,
            ticket.getTechnician() != null ? SimpleUserMapper.toResponse(ticket.getTechnician()) : null,
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getCategory() != null ? ticket.getCategory().name() : null,
            ticket.getPriority() != null ? ticket.getPriority().name() : null,
            ticket.getStatus() != null ? ticket.getStatus().name() : null,
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            ticket.getResolvedAt()
        );
    }
}
