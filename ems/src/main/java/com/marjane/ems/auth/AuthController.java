package com.marjane.ems.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Services.UserService;

/**
 * Authentication controller for login and registration.
 * Issues JWT tokens after successful authentication.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Login endpoint - authenticates user and returns JWT token.
     * Accepts either username, email, or EID.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = findUserByCredential(request.eid());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "EID not found. Please check your employee ID.", null, "INVALID_EID", null));
            }

            // Verify password
            if (!userService.verifyPassword(request.password(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "Incorrect password. Please try again.", null, "INVALID_PASSWORD", null));
            }

            // Check if account is active
            if (!user.getStatus().isActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse(false, "Your account is not active. Please contact support.", null, "ACCOUNT_INACTIVE", null));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(true, "Login successful", token, null, user.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Authentication failed: " + e.getMessage(), null, "AUTH_ERROR", null));
        }
    }

    /**
     * Register new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Validate input
            if (request.username() == null || request.username().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Username is required", null));
            }
            if (request.email() == null || request.email().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Email is required", null));
            }
            if (request.password() == null || request.password().length() < 6) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Password must be at least 6 characters", null));
            }

            Role role = request.role() != null ? request.role() : Role.EMPLOYEE;

            User newUser = userService.registerUser(
                request.username(),
                request.email(),
                request.password(),
                role,
                request.firstName(),
                request.lastName()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(true, "Registration successful. EID: " + newUser.getEid(), newUser.getEid()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new AuthResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponse(false, "Registration failed: " + e.getMessage(), null));
        }
    }

    /**
     * Helper method to find user by username, email, or EID.
     */
    private User findUserByCredential(String credential) {
        if (credential == null || credential.isBlank()) {
            return null;
        }

        return userRepository.findByUsername(credential)
            .or(() -> userRepository.findByEmail(credential))
            .or(() -> userRepository.findByEid(credential))
            .orElse(null);
    }
}