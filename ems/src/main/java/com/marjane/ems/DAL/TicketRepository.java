package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatusIgnoreCase(String status);
    
    List<Ticket> findByPriorityIgnoreCase(String priority);
    
    List<Ticket> findByCategoryIgnoreCase(String category);
    
    List<Ticket> findByCreatorId(Long creatorId);
    
    List<Ticket> findByTechnicianId(Long technicianId);
    
    List<Ticket> findByTechnicianIdNull();

    Long countByStatusIgnoreCase(String status);
}
