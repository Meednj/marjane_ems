package com.marjane.ems.DAL;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.User;

/**
 * Repository for Administrator users.
 * Deprecated - use UserRepository with Role.ADMIN filtering instead.
 * Kept for backward compatibility.
 */
@Repository
public interface AdministratorRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();
}
