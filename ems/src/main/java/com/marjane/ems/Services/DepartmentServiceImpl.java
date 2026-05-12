package com.marjane.ems.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.DepartmentRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.DepartmentRequest;
import com.marjane.ems.DTO.response.DepartmentResponse;
import com.marjane.ems.Entities.Department;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Mapper.DepartmentMapper;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<DepartmentResponse> getAllDepartments() {
    return departmentRepository.findAll()
            .stream()
            .map(dept -> new DepartmentResponse(
                    dept.getId(),
                    dept.getName(),
                    dept.getDescription()
            ))
            .toList();
}

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
    public void deleteDepartment(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        List<User> users = userRepository.findByDepartment(department);

        for (User user : users) {
            user.setDepartment(null);
        }

        userRepository.saveAll(users);

        departmentRepository.delete(department);
    }

    @Override
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return DepartmentMapper.toResponse(department);
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        if (request.name() != null && !request.name().equals(department.getName())) {
            if (departmentRepository.existsByName(request.name())) {
                throw new RuntimeException("Department name already exists");
            }
            department.setName(request.name());
        }
        
        if (request.description() != null) {
            department.setDescription(request.description());
        }
        
        return DepartmentMapper.toResponse(departmentRepository.save(department));
    }
}