package com.marjane.ems.DAL;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marjane.ems.Entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEID(String EID);

    Optional<User> findByEmail(String email);
    
    boolean existsByEIDAndActiveTrue(String EID);

    boolean existsByEID(String EID);
    
    void deleteByEID(String EID);

    
}