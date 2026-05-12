package com.marjane.ems.Services;

import java.util.List;

import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;

public interface DepartmentService {

    public List<DepartmentResponse> getAllDepartments();
    
    public Long countDepartments();

    public DepartmentResponse createDepartment(DepartmentRequest request);

    public void deleteDepartment(Long id);

    public DepartmentResponse getDepartmentById(Long id);

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request);
}