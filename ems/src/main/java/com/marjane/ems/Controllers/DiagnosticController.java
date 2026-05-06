package com.marjane.ems.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Diagnostic controller for debugging authentication and CORS issues.
 */
@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    /**
     * Check authentication status and JWT token validity.
     * This endpoint is accessible to everyone to help diagnose auth issues.
     */
    @GetMapping("/auth-status")
    public Map<String, Object> getAuthStatus(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authHeader = request.getHeader("Authorization");
        
        response.put("isAuthenticated", authentication != null && authentication.isAuthenticated());
        response.put("principal", authentication != null ? authentication.getPrincipal() : "NONE");
        response.put("authorities", authentication != null ? authentication.getAuthorities() : "NONE");
        response.put("authHeader", authHeader != null ? "PRESENT" : "MISSING");
        response.put("authHeaderValue", authHeader);
        response.put("message", "Use this to diagnose 403 errors");
        
        return response;
    }

    /**
     * Simple health check - always works.
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "message", "Backend is running");
    }
}
