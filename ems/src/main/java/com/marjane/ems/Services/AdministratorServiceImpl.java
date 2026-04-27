package com.marjane.ems.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.AdministratorRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Mapper.AdministratorMapper;

/**
 * Legacy Administrator Service Implementation.
 * @deprecated Use UserService instead
 */
@Service
@Deprecated
public class AdministratorServiceImpl extends AbstractUserService<User, AdministratorRequest, AdministratorResponse>
        implements AdministratorService {

    public AdministratorServiceImpl(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    AdministratorRepository administratorRepository) {
        super(userRepository, passwordEncoder);
    }

    @Override
    public AdministratorResponse create(AdministratorRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        User administrator = mapToEntity(request);
        administrator.setPassword(encodePassword(request.password()));
        administrator.setRole(Role.ADMIN);

        return mapToResponse(userRepository.save(administrator));
    }

    @Override
    public AdministratorResponse update(String EID, AdministratorRequest request) {
        User administrator = userRepository.findByEid(EID)
            .filter(user -> user.getRole() == Role.ADMIN)
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
    protected AdministratorResponse mapToResponse(User entity) {
        return AdministratorMapper.toResponse(entity);
    }

    @Override
    protected User mapToEntity(AdministratorRequest request) {
        return AdministratorMapper.toEntity(request);
    }

    @Override
    protected void updateEntityFromRequest(User entity, AdministratorRequest request) {
        AdministratorMapper.updateEntity(entity, request);
    }
}
