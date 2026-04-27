package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.User;

/**
 * Repository for Technician users.
 * Deprecated - use UserRepository with Role.TECHNICIAN filtering instead.
 * Kept for backward compatibility.
 */
@Repository
public interface TechnicianRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.role = 'TECHNICIAN' AND LOWER(u.status) = LOWER(:status)")
    List<User> findByStatusIgnoreCase(@Param("status") String status);
    
    @Query("SELECT u FROM User u WHERE u.role = 'TECHNICIAN'")
    List<User> findAllTechnicians();
}
