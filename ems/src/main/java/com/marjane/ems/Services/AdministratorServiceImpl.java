package com.marjane.ems.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.AdministratorRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import com.marjane.ems.Entities.Administrator;
import com.marjane.ems.Mapper.AdministratorMapper;

@Service
public class AdministratorServiceImpl extends AbstractUserService<Administrator, AdministratorRequest, AdministratorResponse>
        implements AdministratorService {

    private final AdministratorRepository administratorRepository;

    public AdministratorServiceImpl(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    AdministratorRepository administratorRepository) {
        super(userRepository, passwordEncoder);
        this.administratorRepository = administratorRepository;
    }

    @Override
    public AdministratorResponse create(AdministratorRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        Administrator administrator = mapToEntity(request);
        administrator.setPassword(encodePassword(request.password()));

        return mapToResponse(administratorRepository.save(administrator));
    }

    @Override
    public AdministratorResponse update(String EID, AdministratorRequest request) {
        Administrator administrator = userRepository.findByEID(EID)
            .filter(Administrator.class::isInstance)
            .map(Administrator.class::cast)
            .orElseThrow(() -> new RuntimeException("Administrator not found with EID: " + EID));

        if (request.email() != null && !request.email().equals(administrator.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already taken");
            }
        }

        updateEntityFromRequest(administrator, request);
        return mapToResponse(userRepository.save(administrator));
    }

    @Override
    protected AdministratorResponse mapToResponse(Administrator entity) {
        return AdministratorMapper.toResponse(entity);
    }

    @Override
    protected Administrator mapToEntity(AdministratorRequest request) {
        return AdministratorMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(Administrator entity, AdministratorRequest request) {
        AdministratorMapper.updateEntity(entity, request);
    }
}
