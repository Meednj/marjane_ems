package com.marjane.ems.DAL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;

/**
 * Repository for User entity.
 * Provides methods for finding users by various criteria.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEid(String eid);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEid(String eid);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void deleteByEid(String eid);

    List<User> findByRole(Role role);

    List<User> findByStatus(String status);

    long countByRole(Role role);
}