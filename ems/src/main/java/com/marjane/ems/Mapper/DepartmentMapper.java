package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;
import com.marjane.ems.Entities.Department;

public class DepartmentMapper {


    public static Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.name());
        department.setDescription(request.description());
        return department;
    }

    public static DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
            department.getId(),
            department.getName(),
            department.getDescription()
        );
    }
    public static void updateEntity(Department department, DepartmentRequest request) {
        if (request.name() != null) {
            department.setName(request.name());
        }
        if (request.description() != null) {
            department.setDescription(request.description());
        }
    }
}