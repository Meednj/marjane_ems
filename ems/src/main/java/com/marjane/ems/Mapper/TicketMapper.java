package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.DTO.response.TicketResponse;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;

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

        java.util.List<com.marjane.ems.DTO.response.SimpleUserResponse> techs = ticket.getTechnicians() == null
            ? java.util.Collections.emptyList()
            : ticket.getTechnicians()
                .stream()
                .map(SimpleUserMapper::toResponse)
                .toList();

        return new TicketResponse(
            ticket.getId(),
            ticket.getCreator() != null ? SimpleUserMapper.toResponse(ticket.getCreator()) : null,
            techs,
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

    /**
     * Converts a TicketRequest DTO to a Ticket entity.
     */
    public static Ticket toEntity(TicketRequest request) {
        if (request == null) return null;

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCategory(request.category() != null
            ? TicketCategory.valueOf(request.category().toUpperCase())
            : null);
        ticket.setPriority(request.priority() != null
            ? TicketPriority.valueOf(request.priority().toUpperCase())
            : null);
        ticket.setStatus(request.status() != null
            ? TicketStatus.valueOf(request.status().toUpperCase())
            : TicketStatus.PENDING);

        return ticket;
    }

    /**
     * Updates an existing Ticket entity with data from TicketRequest DTO.
     */
    public static void updateEntity(Ticket entity, TicketRequest request) {
        if (request == null || entity == null) return;

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        if (request.category() != null) {
            entity.setCategory(TicketCategory.valueOf(request.category().toUpperCase()));
        }
        if (request.priority() != null) {
            entity.setPriority(TicketPriority.valueOf(request.priority().toUpperCase()));
        }
        if (request.status() != null) {
            entity.setStatus(TicketStatus.valueOf(request.status().toUpperCase()));
        }
    }
}
