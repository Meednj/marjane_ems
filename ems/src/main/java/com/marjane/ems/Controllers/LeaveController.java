package com.marjane.ems.Controllers;

import com.marjane.ems.Services.LeaveService;
import com.marjane.ems.DTO.request.LeaveRequest;
import com.marjane.ems.DTO.response.LeaveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    @Autowired
    private LeaveService leaveService;

    @PostMapping
    public ResponseEntity<LeaveResponse> createLeave(@RequestBody LeaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.createLeave(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<LeaveResponse>> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveService.getLeavesByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(leaveService.getLeavesByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByType(@PathVariable String type) {
        return ResponseEntity.ok(leaveService.getLeavesByType(type));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LeaveResponse>> getLeavesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(leaveService.getLeavesByDateRange(startDate, endDate));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaveRequests() {
        return ResponseEntity.ok(leaveService.getPendingLeaveRequests());
    }

    @GetMapping("/approver/{approverId}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByApprover(@PathVariable Long approverId) {
        return ResponseEntity.ok(leaveService.getLeavesByApprover(approverId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveResponse> updateLeave(@PathVariable Long id, @RequestBody LeaveRequest request) {
        return ResponseEntity.ok(leaveService.updateLeave(id, request));
    }

    @PostMapping("/{leaveId}/approve/{approverId}")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable Long leaveId, @PathVariable Long approverId) {
        return ResponseEntity.ok(leaveService.approveLeave(leaveId, approverId));
    }

    @PostMapping("/{leaveId}/reject/{approverId}")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable Long leaveId, @PathVariable Long approverId) {
        return ResponseEntity.ok(leaveService.rejectLeave(leaveId, approverId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}
