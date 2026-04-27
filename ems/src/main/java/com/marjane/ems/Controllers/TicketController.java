package com.marjane.ems.Controllers;

import com.marjane.ems.Services.TicketService;
import com.marjane.ems.DTO.request.TicketRequest;
import com.marjane.ems.DTO.response.TicketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TicketResponse>> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/count")
    public Long countTickets() {
        return Long.valueOf(ticketService.getAllTickets().size());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }
    @GetMapping("/count/status/{status}")
    public Long countTicketsByStatus(@PathVariable String status) {
        return Long.valueOf(ticketService.getTicketsByStatus(status).size());
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TicketResponse>> getTicketsByPriority(@PathVariable String priority) {
        return ResponseEntity.ok(ticketService.getTicketsByPriority(priority));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TicketResponse>> getTicketsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ticketService.getTicketsByCategory(category));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(ticketService.getTicketsByCreator(creatorId));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByTechnician(@PathVariable Long technicianId) {
        return ResponseEntity.ok(ticketService.getTicketsByTechnician(technicianId));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<TicketResponse>> getUnassignedTickets() {
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable Long id, @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(id, request));
    }

    @PostMapping("/{ticketId}/assign/{technicianId}")
    public ResponseEntity<TicketResponse> assignTicketToTechnician(@PathVariable Long ticketId, @PathVariable Long technicianId) {
        return ResponseEntity.ok(ticketService.assignTicketToTechnician(ticketId, technicianId));
    }

    @PutMapping("/{ticketId}/status/{status}")
    public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable Long ticketId, @PathVariable String status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    /* @GetMapping("/count/status/{status}")
    public Long countTicketsByStatus(@PathVariable String status) {
        return Long.valueOf(ticketService.countTicketsByStatus(status).size());
    } */
}
