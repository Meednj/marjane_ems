package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;
import com.marjane.ems.Entities.User;

/**
 * Factory class for creating Ticket entities.
 * @deprecated Use TicketService instead
 */
@Deprecated
public class TicketFactory {

    /**
     * Creates a Ticket entity from a TicketRequest, creator (Employee), and optional technician (Technician).
     */
    public Ticket createTicket(TicketRequest request, User creator, User technician) {
        if (request == null) {
            throw new IllegalArgumentException("TicketRequest cannot be null");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }

        Ticket ticket = new Ticket();
        ticket.setCreator(creator);
        ticket.setTechnician(technician);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setCategory(TicketCategory.valueOf(request.category().toUpperCase()));
        ticket.setPriority(TicketPriority.valueOf(request.priority().toUpperCase()));
        ticket.setStatus(request.status() != null ? 
            TicketStatus.valueOf(request.status().toUpperCase()) : 
            TicketStatus.PENDING);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticket;
    }
}
