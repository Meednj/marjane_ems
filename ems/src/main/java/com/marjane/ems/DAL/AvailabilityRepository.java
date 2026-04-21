package com.marjane.ems.DAL;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Availability;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    Optional<Availability> findByUserId(Long userId);
    
    List<Availability> findByStatusIgnoreCase(String status);
}
