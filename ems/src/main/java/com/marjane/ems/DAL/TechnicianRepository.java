package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Technician;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    List<Technician> findByStatusIgnoreCase(String status);
}
