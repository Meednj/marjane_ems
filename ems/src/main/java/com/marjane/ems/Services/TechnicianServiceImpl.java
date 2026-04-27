package com.marjane.ems.Services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.TechnicianRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Mapper.TechnicianMapper;

/**
 * Legacy Technician Service Implementation.
 * @deprecated Use UserService instead
 */
@Service
@Deprecated
public class TechnicianServiceImpl extends AbstractUserService<User, TechnicianRequest, TechnicianResponse>
        implements TechnicianService {

    public TechnicianServiceImpl(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                TechnicianRepository technicianRepository) {
        super(userRepository, passwordEncoder);
    }

    @Override
    public TechnicianResponse create(TechnicianRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        User technician = mapToEntity(request);
        technician.setPassword(encodePassword(request.password()));
        technician.setRole(Role.TECHNICIAN);

        return mapToResponse(userRepository.save(technician));
    }

    @Override
    public TechnicianResponse update(String EID, TechnicianRequest request) {
        User technician = userRepository.findByEid(EID)
            .filter(user -> user.getRole() == Role.TECHNICIAN)
            .orElseThrow(() -> new RuntimeException("Technician not found with EID: " + EID));

        if (request.email() != null && !request.email().equals(technician.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already taken");
            }
        }

        updateEntityFromRequest(technician, request);
        return mapToResponse(userRepository.save(technician));
    }

    @Override
    public List<TechnicianResponse> getAvailableTechnicians() {
        List<User> technicians = userRepository.findByRole(Role.TECHNICIAN).stream()
            .filter(user -> user.getStatus() != null && user.getStatus().isActive())
            .toList();

        if (technicians.isEmpty()) {
            throw new RuntimeException("No available technicians found");
        }

        return technicians.stream().map(TechnicianMapper::toResponse).toList();
    }

    @Override
    protected TechnicianResponse mapToResponse(User entity) {
        return TechnicianMapper.toResponse(entity);
    }

    @Override
    protected User mapToEntity(TechnicianRequest request) {
        return TechnicianMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(User entity, TechnicianRequest request) {
        TechnicianMapper.updateEntity(entity, request);
    }
}
