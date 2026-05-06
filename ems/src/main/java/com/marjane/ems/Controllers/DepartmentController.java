package com.marjane.ems.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;
import com.marjane.ems.Services.DepartmentServiceImpl;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentServiceImpl departmentService;

    @GetMapping("/count")
    public Long countDepartments() {
        return departmentService.countDepartments();
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @RequestBody DepartmentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departmentService.createDepartment(request));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(@RequestBody Map<String, String> body) {
        departmentService.deleteDepartment(body.get("name"));
        return ResponseEntity.noContent().build();
    }
}