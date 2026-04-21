package com.marjane.ems.DAL;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Presence;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findByUserId(Long userId);
    
    List<Presence> findByPresenceDate(LocalDate presenceDate);
    
    List<Presence> findByPresenceDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Presence> findByStatusIgnoreCase(String status);
    
    List<Presence> findByShiftId(Long shiftId);
    
    List<Presence> findByUserIdAndPresenceDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
