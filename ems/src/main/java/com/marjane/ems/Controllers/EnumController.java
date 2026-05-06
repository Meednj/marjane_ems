package com.marjane.ems.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enum Controller for retrieving enum values from the backend.
 * Provides options for dropdowns in the frontend.
 */
@RestController
@RequestMapping("/api/enums")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnumController {

    /**
     * Get all UserStatus enum values with their display names
     */
    @GetMapping("/user-statuses")
    public ResponseEntity<List<Map<String, String>>> getUserStatuses() {
        List<Map<String, String>> statuses = Arrays.stream(UserStatus.values())
            .map(status -> Map.of(
                "value", status.name(),
                "label", status.getDisplayName()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get all Role enum values with their display names
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, String>>> getRoles() {
        List<Map<String, String>> roles = Arrays.stream(Role.values())
            .map(role -> Map.of(
                "value", role.name(),
                "label", role.getDisplayName()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(roles);
    }
}
