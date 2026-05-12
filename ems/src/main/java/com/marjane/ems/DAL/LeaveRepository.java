package com.marjane.ems.DAL;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Leave;
import com.marjane.ems.Entities.LeaveStatus;
import com.marjane.ems.Entities.LeaveType;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByUserId(Long userId);
    
    List<Leave> findByStatus(LeaveStatus status);
    
    List<Leave> findByType(LeaveType type);
    
    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Leave> findByApproverId(Long approverId);

    @Query("select l from Leave l where l.user.id = :userId and l.startDate <= :endDate and l.endDate >= :startDate")
    List<Leave> findOverlappingLeaves(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select l from Leave l where l.user.id = :userId and l.id <> :excludeId and l.startDate <= :endDate and l.endDate >= :startDate")
    List<Leave> findOverlappingLeavesExcludingId(@Param("userId") Long userId, @Param("excludeId") Long excludeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
