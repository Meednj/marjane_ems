package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.TicketRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.DTO.response.TicketResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketStatus;
import com.marjane.ems.Mapper.TicketMapper;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TicketResponse createTicket(TicketRequest request) {
        Ticket ticket = TicketMapper.toEntity(request);

        userRepository.findById(request.creatorId())
            .filter(user -> user.getRole() == Role.EMPLOYEE)
            .ifPresentOrElse(
                user -> ticket.setCreator(user),
                () -> {
                    throw new RuntimeException("Creator not found or not an Employee with ID: " + request.creatorId());
                }
            );

        if (request.technicianId() != null) {
            userRepository.findById(request.technicianId())
                .filter(user -> user.getRole() == Role.TECHNICIAN)
                .ifPresentOrElse(
                    user -> ticket.setTechnician(user),
                    () -> {
                        throw new RuntimeException("Technician not found with ID: " + request.technicianId());
                    }
                );
        }

        ticket.setStatus(TicketStatus.PENDING);
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public Optional<TicketResponse> getTicketById(Long id) {
        return ticketRepository.findById(id).map(TicketMapper::toResponse);
    }

    @Override
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll().stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getTicketsByStatus(String status) {
        List<Ticket> tickets = ticketRepository.findByStatusIgnoreCase(status);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found with status: " + status);
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getTicketsByPriority(String priority) {
        List<Ticket> tickets = ticketRepository.findByPriorityIgnoreCase(priority);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found with priority: " + priority);
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getTicketsByCategory(String category) {
        List<Ticket> tickets = ticketRepository.findByCategoryIgnoreCase(category);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found with category: " + category);
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getTicketsByCreator(Long creatorId) {
        List<Ticket> tickets = ticketRepository.findByCreatorId(creatorId);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found for creator ID: " + creatorId);
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getTicketsByTechnician(Long technicianId) {
        List<Ticket> tickets = ticketRepository.findByTechnicianId(technicianId);

        if (tickets.isEmpty()) {
            throw new RuntimeException("No tickets found for technician ID: " + technicianId);
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public List<TicketResponse> getUnassignedTickets() {
        List<Ticket> tickets = ticketRepository.findByTechnicianIdNull();

        if (tickets.isEmpty()) {
            throw new RuntimeException("No unassigned tickets found");
        }

        return tickets.stream().map(TicketMapper::toResponse).toList();
    }

    @Override
    public TicketResponse updateTicket(Long id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));

        TicketMapper.updateEntity(ticket, request);
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse assignTicketToTechnician(Long ticketId, Long technicianId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        User technician = userRepository.findById(technicianId)
            .filter(user -> user.getRole() == Role.TECHNICIAN)
            .orElseThrow(() -> new RuntimeException("Technician not found with ID: " + technicianId));

        ticket.setTechnician(technician);
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse updateTicketStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        ticket.setStatus(TicketStatus.valueOf(status));
        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found with ID: " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public Long countTicketsByStatus(String status) {
        return ticketRepository.countByStatusIgnoreCase(status);
    }
}
