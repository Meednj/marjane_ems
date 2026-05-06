package com.marjane.ems.Mapper;

import java.time.LocalDateTime;
import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Department;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;

/**
 * Mapper class for Employee (User with Role.EMPLOYEE) to EmployeResponse DTO.
 * @deprecated Use UserMapper instead
 */
@Deprecated
public class EmployeMapper {

    /**
     * Converts a User entity (Employee role) to an EmployeResponse DTO.
     */
    public static EmployeResponse toResponse(User employe) {
    if (employe == null) {
        throw new IllegalArgumentException("Employe cannot be null");
    }

    return new EmployeResponse(
            employe.getId(),
            employe.getEid(),
            employe.getLastName(),
            employe.getFirstName(),
            employe.getEmail(),
            employe.getPhone(),
            employe.getRole() != null ? employe.getRole().name() : null,
            employe.getStatus() != null ? employe.getStatus().name() : null,
            employe.getDepartment() != null ? employe.getDepartment().getName() : null,
            employe.getCreatedAt(),
            employe.getUpdatedAt()
    );
}
    
    public static User toEntity(EmployeRequest request, Department department) {
    if (request == null) return null;

    User employe = new User();

    employe.setLastName(request.lastName());
    employe.setFirstName(request.firstName());
    employe.setEmail(request.email());
    employe.setPhone(request.phone());

    // Auto-generate username: first letter of firstName + full lastName, all lowercase
    String username = (request.firstName().charAt(0) + request.lastName()).toLowerCase();
    employe.setUsername(username);

    employe.setStatus(
        request.status() != null
            ? UserStatus.valueOf(request.status().toUpperCase())
            : UserStatus.ACTIVE
    );

    employe.setRole(Role.EMPLOYEE);

    employe.setDepartment(department);

    employe.setCreatedAt(LocalDateTime.now());

    return employe;
}
    public static void updateEntity(User entity, EmployeRequest request, Department department) {
    if (request == null || entity == null) return;

    entity.setLastName(request.lastName());
    entity.setFirstName(request.firstName());
    entity.setEmail(request.email());
    entity.setPhone(request.phone());

    entity.setStatus(
        request.status() != null
            ? UserStatus.valueOf(request.status().toUpperCase())
            : UserStatus.ACTIVE
    );

    if (department != null) {
        entity.setDepartment(department);
    }

    entity.setUpdatedAt(LocalDateTime.now());
}
}
