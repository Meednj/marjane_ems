package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.TeamGroupRepository;
import com.marjane.ems.DAL.TicketRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.DTO.response.TicketResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.TeamGroup;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.UserStatus;
import com.marjane.ems.Mapper.TicketMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TeamGroupRepository teamGroupRepository;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TeamGroupRepository teamGroupRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.teamGroupRepository = teamGroupRepository;
    }

    @Override
    public TicketResponse createTicket(TicketRequest request) {

        Ticket ticket = TicketMapper.toEntity(request);

        User creator = userRepository.findById(request.creatorId())
            .orElseThrow(() ->
                new RuntimeException("Creator not found with ID: " + request.creatorId()));

        ticket.setCreator(creator);

        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.PENDING);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        assignTicketToCategoryTechnicians(savedTicket);

        savedTicket = ticketRepository.findById(savedTicket.getId()).orElse(savedTicket);

        return TicketMapper.toResponse(savedTicket);
    }

    @Override
    public Optional<TicketResponse> getTicketById(Long id) {
        return ticketRepository.findById(id)
            .filter(ticket -> {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null) return true;
                Object principal = auth.getPrincipal();
                if (!(principal instanceof String)) return true;
                String eid = (String) principal;
                return userRepository.findByEid(eid)
                    .map(user -> {
                        if (user.getRole() == Role.TECHNICIAN) {
                            if (user.getTeamGroup() == null || ticket.getCategory() == null) return false;
                            TicketCategory allowed = TicketCategory.valueOf(user.getTeamGroup().getName().name());
                            return allowed == ticket.getCategory();
                        }
                        return true; // ADMIN and others can see
                    }).orElse(true);
            })
            .map(TicketMapper::toResponse);
    }

    @Override
    public List<TicketResponse> getAllTickets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return ticketRepository.findAll().stream().map(TicketMapper::toResponse).toList();
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof String)) {
            return ticketRepository.findAll().stream().map(TicketMapper::toResponse).toList();
        }

        String eid = (String) principal;

        return userRepository.findByEid(eid)
            .map(user -> {
                if (user.getRole() == Role.TECHNICIAN) {
                    if (user.getTeamGroup() == null) return java.util.List.<TicketResponse>of();

                    TicketCategory category = TicketCategory.valueOf(user.getTeamGroup().getName().name());
                    return ticketRepository.findByCategory(category)
                        .stream()
                        .map(TicketMapper::toResponse)
                        .toList();
                }

                // ADMIN and other roles
                return ticketRepository.findAll().stream().map(TicketMapper::toResponse).toList();
            })
            .orElseGet(() -> ticketRepository.findAll().stream().map(TicketMapper::toResponse).toList());
    }

    @Override
    public List<TicketResponse> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatus(status)
            .stream()
            .map(TicketMapper::toResponse)
            .toList();
    }

    @Override
    public List<TicketResponse> getTicketsByPriority(String priority) {

        TicketPriority ticketPriority =
            TicketPriority.valueOf(priority.toUpperCase());

        return ticketRepository.findByPriority(ticketPriority)
            .stream()
            .map(TicketMapper::toResponse)
            .toList();
    }

    @Override
    public List<TicketResponse> getTicketsByCategory(String category) {

        TicketCategory ticketCategory = TicketCategory.valueOf(category.toUpperCase());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return ticketRepository.findByCategory(ticketCategory).stream().map(TicketMapper::toResponse).toList();
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof String)) {
            return ticketRepository.findByCategory(ticketCategory).stream().map(TicketMapper::toResponse).toList();
        }

        String eid = (String) principal;

        return userRepository.findByEid(eid)
            .map(user -> {
                if (user.getRole() == Role.TECHNICIAN) {
                    if (user.getTeamGroup() == null) return java.util.List.<TicketResponse>of();
                    TicketCategory allowed = TicketCategory.valueOf(user.getTeamGroup().getName().name());
                    if (allowed != ticketCategory) return java.util.List.<TicketResponse>of();
                }
                return ticketRepository.findByCategory(ticketCategory).stream().map(TicketMapper::toResponse).toList();
            })
            .orElseGet(() -> ticketRepository.findByCategory(ticketCategory).stream().map(TicketMapper::toResponse).toList());
    }

    @Override
    public List<TicketResponse> getTicketsByCreator(Long creatorId) {
        return ticketRepository.findByCreatorId(creatorId)
            .stream()
            .map(TicketMapper::toResponse)
            .toList();
    }

    @Override
    public List<TicketResponse> getTicketsByTechnician(Long technicianId) {
        return ticketRepository.findByTechnicians_Id(technicianId)
            .stream()
            .map(TicketMapper::toResponse)
            .toList();
    }

    @Override
    public List<TicketResponse> getUnassignedTickets() {
        return ticketRepository.findByTechniciansIsEmpty()
            .stream()
            .map(TicketMapper::toResponse)
            .toList();
    }

    @Override
    public TicketResponse updateTicket(Long id, TicketRequest request) {

        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Ticket not found with ID: " + id));

        TicketCategory oldCategory = ticket.getCategory();

        TicketMapper.updateEntity(ticket, request);

        Ticket updatedTicket = ticketRepository.save(ticket);

        if (oldCategory != updatedTicket.getCategory()) {
            removeTicketFromAllTechnicians(updatedTicket);
            assignTicketToCategoryTechnicians(updatedTicket);
            updatedTicket = ticketRepository.findById(updatedTicket.getId()).orElse(updatedTicket);
        }

        return TicketMapper.toResponse(updatedTicket);
    }

    @Override
    public TicketResponse assignTicketToTechnician(Long ticketId, Long technicianId) {

        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() ->
                new RuntimeException("Ticket not found with ID: " + ticketId));

        User technician = userRepository.findById(technicianId)
            .filter(user -> user.getRole() == Role.TECHNICIAN)
            .filter(user -> user.getStatus() == UserStatus.ACTIVE)
            .orElseThrow(() ->
                new RuntimeException("Technician not found with ID: " + technicianId));

        validateTechnicianCategory(ticket.getCategory(), technician);

        if (!technician.getAssignedTickets().contains(ticket)) {
            technician.getAssignedTickets().add(ticket);
        }

        if (ticket.getTechnicians() == null) ticket.setTechnicians(new java.util.ArrayList<>());
        if (!ticket.getTechnicians().contains(technician)) {
            ticket.getTechnicians().add(technician);
        }

        userRepository.save(technician);
        ticketRepository.save(ticket);

        return TicketMapper.toResponse(ticket);
    }

    @Override
    public TicketResponse updateTicketStatus(Long ticketId, String status) {

        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() ->
                new RuntimeException("Ticket not found with ID: " + ticketId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String principalEid)) {
            throw new AccessDeniedException("Unauthenticated user");
        }

        User actor = userRepository.findByEid(principalEid)
            .orElseThrow(() -> new AccessDeniedException("User not found"));

        boolean isAdmin = actor.getRole() == Role.ADMIN;
        boolean isCreator = ticket.getCreator() != null &&
            ticket.getCreator().getId() != null &&
            ticket.getCreator().getId().equals(actor.getId());
        boolean isAssignedTechnician = ticket.getTechnicians() != null &&
            ticket.getTechnicians().stream().anyMatch(t -> t.getId().equals(actor.getId()));

        if (!(isAdmin || isCreator || isAssignedTechnician)) {
            throw new AccessDeniedException("You are not allowed to change this ticket status");
        }

        TicketStatus nextStatus = TicketStatus.valueOf(status.toUpperCase());

        if (nextStatus == TicketStatus.CLOSED) {
            ticket.setStatus(TicketStatus.CLOSED);
            TicketResponse closedResponse = TicketMapper.toResponse(ticket);

            removeTicketFromAllTechnicians(ticket);
            ticketRepository.delete(ticket);

            return closedResponse;
        }

        ticket.setStatus(nextStatus);

        return TicketMapper.toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {

        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Ticket not found with ID: " + id));

        removeTicketFromAllTechnicians(ticket);

        ticketRepository.delete(ticket);
    }

    @Override
    public Long countTicketsByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    private void assignTicketToCategoryTechnicians(Ticket ticket) {

        if (ticket.getCategory() == null) {
            return;
        }

        TeamGroupName groupName =
            TeamGroupName.valueOf(ticket.getCategory().name());

        TeamGroup teamGroup = teamGroupRepository.findByName(groupName)
            .orElseThrow(() ->
                new RuntimeException("Team group not found: " + groupName));

        List<User> technicians = teamGroup.getTechnicians();

        for (User technician : technicians) {

            if (technician.getRole() == Role.TECHNICIAN &&
                technician.getStatus() == UserStatus.ACTIVE) {

                if (!technician.getAssignedTickets().contains(ticket)) {
                    technician.getAssignedTickets().add(ticket);
                }
            }
        }

        userRepository.saveAll(technicians);
    }

    private void removeTicketFromAllTechnicians(Ticket ticket) {

        List<User> technicians = userRepository.findAll()
            .stream()
            .filter(user -> user.getAssignedTickets() != null)
            .filter(user -> user.getAssignedTickets().contains(ticket))
            .toList();

        for (User technician : technicians) {
            technician.getAssignedTickets().remove(ticket);
        }

        userRepository.saveAll(technicians);
    }

    private void validateTechnicianCategory(
            TicketCategory category,
            User technician) {

        if (category == null) {
            throw new RuntimeException(
                "Ticket category is required before assigning a technician");
        }

        TeamGroupName expectedGroup =
            TeamGroupName.valueOf(category.name());

        if (technician.getTeamGroup() == null ||
            technician.getTeamGroup().getName() != expectedGroup) {

            throw new RuntimeException(
                "Technician does not belong to the " +
                expectedGroup +
                " group");
        }
    }
}