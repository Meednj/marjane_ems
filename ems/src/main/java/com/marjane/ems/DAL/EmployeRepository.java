package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.UserStatus;

/**
 * Repository for Employee users.
 * Deprecated - use UserRepository with Role.EMPLOYEE filtering instead.
 * Kept for backward compatibility.
 */
@Repository
public interface EmployeRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.role = 'EMPLOYEE' AND u.department = :department")
    List<User> findByDepartement(@Param("department") String department);

    // Legacy query - use UserRepository.findByRole() and filter by status instead
    @Deprecated
    @Query("SELECT u FROM User u WHERE u.role = 'EMPLOYEE' AND u.status = :status")
    List<User> findByStatusIgnoreCase(@Param("status") UserStatus status);


}
