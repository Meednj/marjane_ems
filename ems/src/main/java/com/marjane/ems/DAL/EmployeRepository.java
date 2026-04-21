package com.marjane.ems.DAL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.*;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    List<Employe> findByDepartement(String departement);

    List<Employe> findByActivityStatusIgnoreCase(String activityStatus);
    
}
