package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.DTO.response.TicketResponse;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request);
    Optional<TicketResponse> getTicketById(Long id);
    List<TicketResponse> getAllTickets();
    List<TicketResponse> getTicketsByStatus(String status);
    List<TicketResponse> getTicketsByPriority(String priority);
    List<TicketResponse> getTicketsByCategory(String category);
    List<TicketResponse> getTicketsByCreator(Long creatorId);
    List<TicketResponse> getTicketsByTechnician(Long technicianId);
    List<TicketResponse> getUnassignedTickets();
    TicketResponse updateTicket(Long id, TicketRequest request);
    TicketResponse assignTicketToTechnician(Long ticketId, Long technicianId);
    TicketResponse updateTicketStatus(Long ticketId, String status);
    void deleteTicket(Long id);
}
