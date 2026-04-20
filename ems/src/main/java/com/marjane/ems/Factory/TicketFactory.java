package com.marjane.ems.Factory;

import java.time.LocalDateTime;

import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;
import com.marjane.ems.Entities.Employe;
import com.marjane.ems.Entities.Technician;

/**
 * Factory class for creating Ticket entities.
 */
public class TicketFactory {

    /**
     * Creates a Ticket entity from a TicketRequest, creator, and optional technician.
     */
    public Ticket createTicket(TicketRequest request, Employe creator, Technician technician) {
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
