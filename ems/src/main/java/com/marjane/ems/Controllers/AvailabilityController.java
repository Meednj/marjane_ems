package com.marjane.ems.Controllers;

import com.marjane.ems.Services.AvailabilityService;
import com.marjane.ems.DTO.request.AvailabilityRequest;
import com.marjane.ems.DTO.response.AvailabilityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {
    @Autowired
    private AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(@RequestBody AvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityService.createAvailability(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<AvailabilityResponse>> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<AvailabilityResponse>> getAvailabilityByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AvailabilityResponse>> getAvailabilitiesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(@PathVariable Long id, @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, request));
    }

    @PutMapping("/user/{userId}/status/{status}")
    public ResponseEntity<AvailabilityResponse> updateUserAvailabilityStatus(@PathVariable Long userId, @PathVariable String status) {
        return ResponseEntity.ok(availabilityService.updateUserAvailabilityStatus(userId, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
