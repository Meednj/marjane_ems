package com.marjane.ems.Controllers;

import com.marjane.ems.Services.EmployeService;
import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeController {
    @Autowired
    private EmployeService employeService;

    @PostMapping
    public ResponseEntity<EmployeResponse> createEmploye(@RequestBody EmployeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeService.create(request));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Optional<EmployeResponse>> getEmployeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(employeService.getByEmail(email));
    }

    @GetMapping("/{eid}")
    public ResponseEntity<Optional<EmployeResponse>> getEmployeByEID(@PathVariable String eid) {
        return ResponseEntity.ok(employeService.getByEID(eid));
    }

    @GetMapping
    public ResponseEntity<List<EmployeResponse>> getAllEmployes() {
        return ResponseEntity.ok(employeService.getAll());
    }

    @GetMapping("/department/{departement}")
    public ResponseEntity<List<EmployeResponse>> getEmployesByDepartement(@PathVariable String departement) {
        return ResponseEntity.ok(employeService.getByDepartement(departement));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeResponse>> getEmployesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(employeService.getByActivityStatus(status));
    }

    @PutMapping("/{eid}")
    public ResponseEntity<EmployeResponse> updateEmploye(@PathVariable String eid, @RequestBody EmployeRequest request) {
        return ResponseEntity.ok(employeService.update(eid, request));
    }

    @DeleteMapping("/{eid}")
    public ResponseEntity<Void> deleteEmploye(@PathVariable String eid) {
        employeService.delete(eid);
        return ResponseEntity.noContent().build();
    }
}
