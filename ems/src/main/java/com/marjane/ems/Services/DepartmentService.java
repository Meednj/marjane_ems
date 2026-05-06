package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;

public interface DepartmentService {

    public Long countDepartments();

    public DepartmentResponse createDepartment(DepartmentRequest request);

    public void deleteDepartment(String name);
}