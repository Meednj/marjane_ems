package com.marjane.ems.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.DepartmentRepository;
import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;
import com.marjane.ems.Entities.Department;
import com.marjane.ems.Mapper.DepartmentMapper;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Long countDepartments() {
        return departmentRepository.count();
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {

        if (departmentRepository.existsByName(request.name())) {
            throw new RuntimeException("Department already exists");
        }

        Department department = DepartmentMapper.toEntity(request);

        return DepartmentMapper.toResponse(
            departmentRepository.save(department)
        );
    }

    @Override
    public void deleteDepartment(String name) {
        Long id = departmentRepository.findIdByName(name);
        if (id == null) {
            throw new RuntimeException("Department not found");
        }
        departmentRepository.deleteById(id);
    }

}