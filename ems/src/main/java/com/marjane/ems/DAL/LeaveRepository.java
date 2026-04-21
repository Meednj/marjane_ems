package com.marjane.ems.DAL;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Leave;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByUserId(Long userId);
    
    List<Leave> findByStatusIgnoreCase(String status);
    
    List<Leave> findByTypeIgnoreCase(String type);
    
    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Leave> findByApproverId(Long approverId);
}
