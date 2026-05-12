package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Ticket;
import com.marjane.ems.Entities.TicketCategory;
import com.marjane.ems.Entities.TicketPriority;
import com.marjane.ems.Entities.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatus(TicketStatus status);
    
    List<Ticket> findByPriority(TicketPriority priority);
    
    List<Ticket> findByCategory(TicketCategory category);
    
    List<Ticket> findByCreatorId(Long creatorId);
    
    List<Ticket> findByTechnicians_Id(Long technicianId);

    List<Ticket> findByTechniciansIsEmpty();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    Long countByStatus(@Param("status") TicketStatus status);
}
