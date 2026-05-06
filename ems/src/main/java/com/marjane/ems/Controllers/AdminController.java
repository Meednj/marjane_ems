package com.marjane.ems.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Services.EIDGeneratorService;
import com.marjane.ems.Services.UserService;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller for administrative operations.
 * Requires ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EIDGeneratorService eidGeneratorService;

    /**
     * Create a new user (admin endpoint).
     * Automatically hashes the password.
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            User newUser = userService.registerUser(
                request.username(),
                request.email(),
                request.password(),
                request.role() != null ? request.role() : Role.EMPLOYEE,
                request.firstName(),
                request.lastName()
            );

            return ResponseEntity.ok(Map.of(
                "message", "User created successfully",
                "eid", newUser.getEid(),
                "id", newUser.getId()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Migrate all plain-text passwords to BCrypt hashed passwords.
     * This is a one-time operation to fix existing users created directly in database.
     * WARNING: Only run this once!
     */
    @PostMapping("/migrate-passwords")
    @Transactional
    public ResponseEntity<?> migratePasswordsToBcrypt() {
        try {
            List<User> allUsers = userRepository.findAll();
            int migratedCount = 0;
            int skipCount = 0;

            for (User user : allUsers) {
                String password = user.getPassword();
                
                // Skip if password is already hashed (BCrypt hashes start with $2a$, $2b$, $2y$)
                if (password != null && !password.startsWith("$2")) {
                    // Hash the plain-text password
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                    migratedCount++;
                } else {
                    skipCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "message", "Password migration completed",
                "migratedCount", migratedCount,
                "alreadyHashedCount", skipCount,
                "totalCount", allUsers.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Migration failed: " + e.getMessage()));
        }
    }

    /**
     * Generate missing EIDs for users who don't have them yet.
     * Useful for fixing users created manually in the database without EIDs.
     */
    @PostMapping("/generate-missing-eids")
    @Transactional
    public ResponseEntity<?> generateMissingEIDs() {
        try {
            List<User> allUsers = userRepository.findAll();
            int generatedCount = 0;
            int skipCount = 0;

            for (User user : allUsers) {
                if (user.getEid() == null || user.getEid().isBlank()) {
                    // Generate EID for this user
                    String newEid = eidGeneratorService.generateEID(user.getRole());
                    user.setEid(newEid);
                    userRepository.save(user);
                    generatedCount++;
                } else {
                    skipCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "message", "Missing EID generation completed",
                "generatedCount", generatedCount,
                "alreadyHasEIDCount", skipCount,
                "totalCount", allUsers.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "EID generation failed: " + e.getMessage()));
        }
    }

    /**
     * Request DTO for creating users.
     */
    record CreateUserRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        Role role
    ) {}
}
