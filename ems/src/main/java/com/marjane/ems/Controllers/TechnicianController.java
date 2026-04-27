package com.marjane.ems.Controllers;

import com.marjane.ems.Services.TechnicianService;
import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {
    @Autowired
    private TechnicianService technicianService;

    @PostMapping
    public ResponseEntity<TechnicianResponse> createTechnician(@RequestBody TechnicianRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technicianService.create(request));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Optional<TechnicianResponse>> getTechnicianByEmail(@PathVariable String email) {
        return ResponseEntity.ok(technicianService.getByEmail(email));
    }

    @GetMapping("/{eid}")
    public ResponseEntity<Optional<TechnicianResponse>> getTechnicianByEID(@PathVariable String eid) {
        return ResponseEntity.ok(technicianService.getByEID(eid));
    }

    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians() {
        return ResponseEntity.ok(technicianService.getAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<TechnicianResponse>> getAvailableTechnicians() {
        return ResponseEntity.ok(technicianService.getAvailableTechnicians());
    }

    @PutMapping("/{eid}")
    public ResponseEntity<TechnicianResponse> updateTechnician(@PathVariable String eid, @RequestBody TechnicianRequest request) {
        return ResponseEntity.ok(technicianService.update(eid, request));
    }

    @DeleteMapping("/{eid}")
    public ResponseEntity<Void> deleteTechnician(@PathVariable String eid) {
        technicianService.delete(eid);
        return ResponseEntity.noContent().build();
    }
}
