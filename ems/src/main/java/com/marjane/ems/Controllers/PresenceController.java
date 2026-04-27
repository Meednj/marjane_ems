package com.marjane.ems.Controllers;

import com.marjane.ems.Services.PresenceService;
import com.marjane.ems.DTO.request.PresenceRequest;
import com.marjane.ems.DTO.response.PresenceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/presence")
public class PresenceController {
    @Autowired
    private PresenceService presenceService;

    @PostMapping
    public ResponseEntity<PresenceResponse> createPresence(@RequestBody PresenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(presenceService.createPresence(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PresenceResponse>> getPresenceById(@PathVariable Long id) {
        return ResponseEntity.ok(presenceService.getPresenceById(id));
    }

    @GetMapping
    public ResponseEntity<List<PresenceResponse>> getAllPresences() {
        return ResponseEntity.ok(presenceService.getAllPresences());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PresenceResponse>> getPresencesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(presenceService.getPresencesByUser(userId));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<PresenceResponse>> getPresencesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(presenceService.getPresencesByDate(date));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PresenceResponse>> getPresencesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(presenceService.getPresencesByDateRange(startDate, endDate));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PresenceResponse>> getPresencesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(presenceService.getPresencesByStatus(status));
    }

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<List<PresenceResponse>> getPresencesByShift(@PathVariable Long shiftId) {
        return ResponseEntity.ok(presenceService.getPresencesByShift(shiftId));
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<PresenceResponse>> getUserPresenceByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(presenceService.getUserPresenceByDateRange(userId, startDate, endDate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresenceResponse> updatePresence(@PathVariable Long id, @RequestBody PresenceRequest request) {
        return ResponseEntity.ok(presenceService.updatePresence(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresence(@PathVariable Long id) {
        presenceService.deletePresence(id);
        return ResponseEntity.noContent().build();
    }
}
