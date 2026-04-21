package com.marjane.ems.Services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.TechnicianRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.Technician;
import com.marjane.ems.Mapper.TechnicianMapper;

@Service
public class TechnicianServiceImpl extends AbstractUserService<Technician, TechnicianRequest, TechnicianResponse>
        implements TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianServiceImpl(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                TechnicianRepository technicianRepository) {
        super(userRepository, passwordEncoder);
        this.technicianRepository = technicianRepository;
    }

    @Override
    public TechnicianResponse create(TechnicianRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        Technician technician = mapToEntity(request);
        technician.setPassword(encodePassword(request.password()));

        return mapToResponse(technicianRepository.save(technician));
    }

    @Override
    public TechnicianResponse update(String EID, TechnicianRequest request) {
        Technician technician = userRepository.findByEID(EID)
            .filter(Technician.class::isInstance)
            .map(Technician.class::cast)
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
        List<Technician> technicians = technicianRepository.findByStatusIgnoreCase("active");

        if (technicians.isEmpty()) {
            throw new RuntimeException("No available technicians found");
        }

        return technicians.stream().map(TechnicianMapper::toResponse).toList();
    }

    @Override
    protected TechnicianResponse mapToResponse(Technician entity) {
        return TechnicianMapper.toResponse(entity);
    }

    @Override
    protected Technician mapToEntity(TechnicianRequest request) {
        return TechnicianMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(Technician entity, TechnicianRequest request) {
        TechnicianMapper.updateEntity(entity, request);
    }
}
