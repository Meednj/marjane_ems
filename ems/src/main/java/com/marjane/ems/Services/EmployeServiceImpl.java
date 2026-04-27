package com.marjane.ems.Services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.EmployeRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;
import com.marjane.ems.Mapper.EmployeMapper;

/**
 * Legacy Employee Service Implementation.
 * @deprecated Use UserService instead
 */
@Service
@Deprecated
public class EmployeServiceImpl extends AbstractUserService<User, EmployeRequest, EmployeResponse>
        implements EmployeService {

    public EmployeServiceImpl(UserRepository userRepository, 
                              PasswordEncoder passwordEncoder, 
                              EmployeRepository employeRepository) {
        super(userRepository, passwordEncoder);
    }

    @Override
    public EmployeResponse create(EmployeRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        User employe = mapToEntity(request);
        employe.setPassword(encodePassword(request.password()));
        employe.setRole(Role.EMPLOYEE);
        
        return mapToResponse(userRepository.save(employe));
    }
    @Override
    public EmployeResponse update(String EID, EmployeRequest request) {
        User employe = userRepository.findByEid(EID)
            .filter(user -> user.getRole() == Role.EMPLOYEE)
            .orElseThrow(() -> new RuntimeException("Employee not found with EID: " + EID));

        if (request.email() != null && !request.email().equals(employe.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already taken");
            }
        }

        updateEntityFromRequest(employe, request);
        return mapToResponse(userRepository.save(employe));
    }
    @Override
    public List<EmployeResponse> getByDepartement(String departement) {
        List<User> employes = userRepository.findByRole(Role.EMPLOYEE).stream()
            .filter(user -> user.getDepartment() != null && user.getDepartment().equals(departement))
            .toList();
        
        if (employes.isEmpty()) {
            throw new RuntimeException("No employes found for departement: " + departement);
        }
        return employes.stream().map(EmployeMapper::toResponse).toList();
    }

    @Override
    public List<EmployeResponse> getByActivityStatus(String statusName) {
        try {
            UserStatus status = UserStatus.valueOf(statusName.toUpperCase());
            List<User> employes = userRepository.findByRole(Role.EMPLOYEE).stream()
                .filter(user -> user.getStatus() == status)
                .toList();

            if (employes.isEmpty()) {
                throw new RuntimeException("No employes found with status: " + statusName);
            }

            return employes.stream().map(EmployeMapper::toResponse).toList();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + statusName);
        }
    }

    @Override
    protected EmployeResponse mapToResponse(User entity) {
        return EmployeMapper.toResponse(entity);
    }

    @Override
    protected User mapToEntity(EmployeRequest request) {
        return EmployeMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(User entity, EmployeRequest request) {
        EmployeMapper.updateEntity(entity, request);
    }
}