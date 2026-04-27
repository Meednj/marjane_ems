package com.marjane.ems.Controllers;

import com.marjane.ems.Services.AdministratorService;
import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

    @PostMapping
    public ResponseEntity<AdministratorResponse> createAdministrator(@RequestBody AdministratorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administratorService.create(request));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Optional<AdministratorResponse>> getAdministratorByEmail(@PathVariable String email) {
        return ResponseEntity.ok(administratorService.getByEmail(email));
    }

    @GetMapping("/{eid}")
    public ResponseEntity<Optional<AdministratorResponse>> getAdministratorByEID(@PathVariable String eid) {
        return ResponseEntity.ok(administratorService.getByEID(eid));
    }

    @GetMapping
    public ResponseEntity<List<AdministratorResponse>> getAllAdministrators() {
        return ResponseEntity.ok(administratorService.getAll());
    }

    @PutMapping("/{eid}")
    public ResponseEntity<AdministratorResponse> updateAdministrator(@PathVariable String eid, @RequestBody AdministratorRequest request) {
        return ResponseEntity.ok(administratorService.update(eid, request));
    }

    @DeleteMapping("/{eid}")
    public ResponseEntity<Void> deleteAdministrator(@PathVariable String eid) {
        administratorService.delete(eid);
        return ResponseEntity.noContent().build();
    }
}
