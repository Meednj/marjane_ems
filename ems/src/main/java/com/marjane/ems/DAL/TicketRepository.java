package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatusIgnoreCase(TicketStatus status);
    
    List<Ticket> findByPriorityIgnoreCase(String priority);
    
    List<Ticket> findByCategoryIgnoreCase(String category);
    
    List<Ticket> findByCreatorId(Long creatorId);
    
    List<Ticket> findByTechnicianId(Long technicianId);
    
    List<Ticket> findByTechnicianIdNull();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    Long countByStatus(@Param("status") TicketStatus status);
}
