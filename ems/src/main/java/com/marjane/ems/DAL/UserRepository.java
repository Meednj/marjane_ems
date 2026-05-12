package com.marjane.ems.DAL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Department;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.UserStatus;

/**
 * Repository for User entity.
 * Provides methods for finding users by various criteria.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEid(String eid);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByDepartment(Department department);

    List<User> findByTeamGroup_NameAndRole(TeamGroupName name, Role role);

    List<User> findByTeamGroup_NameAndRoleAndStatus(TeamGroupName name, Role role, UserStatus status);

    long countByTeamGroup_NameAndRole(TeamGroupName name, Role role);

    boolean existsByEid(String eid);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void deleteByEid(String eid);

    List<User> findByRole(Role role);

    List<User> findByStatus(String status);

    long countByRole(Role role);
}