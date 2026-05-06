package com.marjane.ems.Entities;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PostLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Services.EIDGeneratorService;

/**
 * JPA EntityListener for User entity.
 * Automatically generates EID before persisting if not already set.
 */
@Component
public class UserEntityListener {

    // Static references for JPA callback
    private static UserRepository userRepository;
    private static EIDGeneratorService eidGeneratorService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        UserEntityListener.userRepository = userRepository;
    }

    @Autowired
    public void setEIDGeneratorService(EIDGeneratorService eidGeneratorService) {
        UserEntityListener.eidGeneratorService = eidGeneratorService;
    }

    /**
     * Called before the entity is persisted.
     * Auto-generates EID if not already set.
     */
    @PrePersist
    public void prePersist(User user) {
        if (user.getEid() == null || user.getEid().isBlank()) {
            if (eidGeneratorService != null && user.getRole() != null) {
                user.setEid(eidGeneratorService.generateEID(user.getRole()));
            }
        }
    }
}
