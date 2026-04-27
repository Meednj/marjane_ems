package com.marjane.ems.Controllers;

import com.marjane.ems.Services.ShiftService;
import com.marjane.ems.Entities.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    @Autowired
    private ShiftService shiftService;

    @PostMapping
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.createShift(shift));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Shift>> getShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(shiftService.getShiftById(id));
    }

    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(shiftService.getAllShifts());
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Shift>> getShiftsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(shiftService.getShiftsByDate(date));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Shift>> getShiftsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(shiftService.getShiftsByDateRange(startDate, endDate));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Shift>> getShiftsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(shiftService.getShiftsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        return ResponseEntity.ok(shiftService.updateShift(id, shift));
    }

    @PostMapping("/{shiftId}/assign/{userId}")
    public ResponseEntity<Shift> assignUserToShift(@PathVariable Long shiftId, @PathVariable Long userId) {
        return ResponseEntity.ok(shiftService.assignUserToShift(shiftId, userId));
    }

    @PostMapping("/{shiftId}/remove/{userId}")
    public ResponseEntity<Shift> removeUserFromShift(@PathVariable Long shiftId, @PathVariable Long userId) {
        return ResponseEntity.ok(shiftService.removeUserFromShift(shiftId, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
