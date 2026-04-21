package com.marjane.ems.DAL;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Shift;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByDateShift(LocalDate dateShift);
    
    List<Shift> findByDateShiftBetween(LocalDate startDate, LocalDate endDate);
}
