package com.marjane.ems.Services;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing User operations.
 * Handles creation, updates, deletion, and queries for users.
 * Replaces multiple user-type specific services with a single, unified approach.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EIDGeneratorService eidGeneratorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user with the system.
     * Auto-generates EID based on role.
     */
    @Transactional
    public User registerUser(String username, String email, String password,
                            Role role, String firstName, String lastName) {
        // Validate unique constraints
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setStatus(UserStatus.ACTIVE);

        // Generate sequential EID
        String eid = eidGeneratorService.generateEID(role);
        user.setEid(eid);

        return userRepository.save(user);
    }

    /**
     * Register an employee with additional fields.
     */
    @Transactional
    public User registerEmployee(String username, String email, String password,
                                String firstName, String lastName,
                                String department, UserStatus status) {
        User user = registerUser(username, email, password, Role.EMPLOYEE, firstName, lastName);
        user.setDepartment(department);
        user.setStatus(status != null ? status : UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username.
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Get user by EID.
     */
    public Optional<User> getUserByEid(String eid) {
        return userRepository.findByEid(eid);
    }

    /**
     * Get all users by role.
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get all active users.
     */
    public List<User> getActiveUsers() {
        return userRepository.findByStatus("ACTIVE");
    }

    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update user profile.
     */
    @Transactional
    public User updateUser(Long id, String firstName, String lastName, 
                          String phone, String department) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (phone != null) user.setPhone(phone);
        if (department != null && user.getRole() == Role.EMPLOYEE) {
            user.setDepartment(department);
        }

        return userRepository.save(user);
    }

    /**
     * Update user password.
     */
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Update user status (ACTIVE, INACTIVE, SUSPENDED, etc.).
     */
    @Transactional
    public User updateStatus(Long id, String status) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setStatus(UserStatus.valueOf(status.toUpperCase()));
        return userRepository.save(user);
    }

    /**
     * Change user role.
     * Note: EID remains unchanged (not regenerated).
     */
    @Transactional
    public User changeRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Delete user by ID.
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Delete user by EID.
     */
    @Transactional
    public void deleteUserByEid(String eid) {
        userRepository.deleteByEid(eid);
    }

    /**
     * Count users by role.
     */
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * Verify password matches.
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Check if username is available.
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Check if email is available.
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public Long countAll() {
        return userRepository.count();
    }
}
