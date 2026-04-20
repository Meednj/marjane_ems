package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.UserRequest;
import com.marjane.ems.DTO.response.UserResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Factory.UserFactory;
import com.marjane.ems.Mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserFactory userFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactory = userFactory;
    }

    // =========================
    // CREATE USER
    // =========================
    @Override
    public UserResponse createUser(UserRequest request) {

        validateUserRequest(request);

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + request.email());
        }

        // 1. Create user via factory
        User user = userFactory.createUser(request, request.role());

        // 2. Encode password (SERVICE responsibility)
        user.setPassword(passwordEncoder.encode(request.password()));

        // 3. Save
        User savedUser = userRepository.save(user);

        // 4. Map response
        return UserMapper.toResponse(savedUser);
    }

    // =========================
    // GET BY ID
    // =========================
    @Override
    public Optional<UserResponse> getUserById(Long id) {
        validateId(id);
        return userRepository.findById(id).map(UserMapper::toResponse);
    }

    // =========================
    // GET BY EMAIL
    // =========================
    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        return userRepository.findByEmail(email).map(UserMapper::toResponse);
    }

    // =========================
    // GET BY EID
    // =========================
    @Override
    public Optional<UserResponse> getUserByEID(String eid) {
        if (eid == null || eid.isBlank()) {
            throw new IllegalArgumentException("EID cannot be empty");
        }
        return userRepository.findByEID(eid).map(UserMapper::toResponse);
    }

    // =========================
    // GET ALL USERS
    // =========================
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // =========================
    // UPDATE USER
    // =========================
    @Override
    public UserResponse updateUser(Long id, UserRequest request) {

        validateId(id);
        validateUserRequest(request);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLastName(request.lastName());
        user.setFirstName(request.firstName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User updated = userRepository.save(user);

        return UserMapper.toResponse(updated);
    }

    // =========================
    // DELETE USER
    // =========================
    @Override
    public void deleteUser(Long id) {

        validateId(id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    // =========================
    // VALIDATION
    // =========================
    private void validateUserRequest(UserRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.lastName().isBlank() ||
            request.firstName().isBlank() ||
            request.email().isBlank() ||
            request.phone().isBlank() ||
            request.password().isBlank() ||
            request.role().isBlank()) {

            throw new IllegalArgumentException("Missing required fields");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
    }
}