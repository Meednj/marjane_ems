package com.marjane.ems.Services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.marjane.ems.DAL.EmployeRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import com.marjane.ems.Entities.Employe;
import com.marjane.ems.Mapper.EmployeMapper;

@Service
public class EmployeServiceImpl extends AbstractUserService<Employe, EmployeRequest, EmployeResponse>
        implements EmployeService {

    private final EmployeRepository employeRepository;
    public EmployeServiceImpl(UserRepository userRepository, 
                              PasswordEncoder passwordEncoder, 
                              EmployeRepository employeRepository) {
        super(userRepository, passwordEncoder);
        this.employeRepository = employeRepository;
    }

    @Override
    public EmployeResponse create(EmployeRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        Employe employe = mapToEntity(request);
        employe.setPassword(encodePassword(request.password()));
        
        // Use employeRepository for specific child persistence
        return mapToResponse(employeRepository.save(employe));
    }
    @Override
    public EmployeResponse update(String EID, EmployeRequest request) {
        // Optimization: One database call instead of two
        Employe employe = userRepository.findByEID(EID)
            .filter(Employe.class::isInstance)
            .map(Employe.class::cast)
            .orElseThrow(() -> new RuntimeException("Employee not found with EID: " + EID));

        // Check email uniqueness if it's being changed
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
        // Use the specific repository! It's much faster than filtering manually.
        List<Employe> employes = employeRepository.findByDepartement(departement);
        
        if (employes.isEmpty()) {
            throw new RuntimeException("No employes found for departement: " + departement);
        }
        return employes.stream().map(EmployeMapper::toResponse).toList();
    }

    @Override
    public List<EmployeResponse> getByActivityStatus(String activityStatus) {
        List<Employe> employes = employeRepository.findByActivityStatusIgnoreCase(activityStatus);

        if (employes.isEmpty()) {
            throw new RuntimeException("No employes found with status: " + activityStatus);
        }

        return employes.stream().map(EmployeMapper::toResponse).toList();
    }

    @Override
    protected EmployeResponse mapToResponse(Employe entity) {
        return EmployeMapper.toResponse(entity);
    }

    @Override
    protected Employe mapToEntity(EmployeRequest request) {
        return EmployeMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(Employe entity, EmployeRequest request) {
        EmployeMapper.updateEntity(entity, request);
    }
}