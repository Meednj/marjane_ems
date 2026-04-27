package com.marjane.ems.Services;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
public abstract class AbstractUserService<T extends User, REQ, RES> implements BaseUserService<REQ, RES> {

    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    
    // Abstract mappers to be defined by child services
    protected abstract RES mapToResponse(T entity);
    protected abstract T mapToEntity(REQ request);
    protected abstract void updateEntityFromRequest(T entity, REQ request);

    @Override
    public Optional<RES> getByEID(String EID) {
        validateEID(EID);
        return userRepository.findByEid(EID).map(this::mapToResponseGeneric);
    }

    @Override
    public Optional<RES> getByEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");
        return userRepository.findByEmail(email).map(this::mapToResponseGeneric);
    }


    @Override
    public List<RES> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseGeneric)
                .toList();
    }

    @Override
    public void delete(String EID) {
        validateEID(EID);
        if (!userRepository.existsByEid(EID)) throw new RuntimeException("User not found");
        userRepository.deleteByEid(EID);
    }

    // Common Logic for password hashing
    protected String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) return null;
        return passwordEncoder.encode(rawPassword);
    }

    // Helper to cast base User entity back to specific type T for mapping
    @SuppressWarnings("unchecked")
    private RES mapToResponseGeneric(User user) {
        return mapToResponse((T) user);
    }

    protected void validateEID(String EID) {
        if (EID == null || EID.isBlank()) throw new IllegalArgumentException("Invalid EID");
    }
}