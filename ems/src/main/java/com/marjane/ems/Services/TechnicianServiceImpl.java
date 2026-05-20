package com.marjane.ems.Services;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.TechnicianRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import com.marjane.ems.Entities.TeamGroupName;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import com.marjane.ems.Entities.UserStatus;
import com.marjane.ems.Mapper.TechnicianMapper;

/**
 * Legacy Technician Service Implementation.
 * @deprecated Use UserService instead
 */
@Service
@Deprecated
public class TechnicianServiceImpl extends AbstractUserService<User, TechnicianRequest, TechnicianResponse>
        implements TechnicianService {

    private final TeamGroupService teamGroupService;

    public TechnicianServiceImpl(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                TechnicianRepository technicianRepository,
                                TeamGroupService teamGroupService) {
        super(userRepository, passwordEncoder);
        this.teamGroupService = teamGroupService;
    }

    @Override
    public TechnicianResponse create(TechnicianRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already taken");
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        User technician = mapToEntity(request);
        // Ensure username is set (database requires non-null username)
        technician.setUsername(request.email());
        technician.setPassword(encodePassword(request.password()));
        technician.setRole(Role.TECHNICIAN);
        
        TeamGroupName groupName = resolveTeamGroupName(request.teamGroup());
        technician.setTeamGroup(teamGroupService.getTeamGroupEntity(groupName));

        TechnicianResponse response = mapToResponse(userRepository.save(technician));
        teamGroupService.refreshAllGroupCounts();
        return response;
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
        // Keep username in sync with email when updated via API
        if (request.email() != null && !request.email().isBlank()) {
            technician.setUsername(request.email());
        }

        TeamGroupName groupName = resolveTeamGroupName(request.teamGroup());
        technician.setTeamGroup(teamGroupService.getTeamGroupEntity(groupName));

        TechnicianResponse response = mapToResponse(userRepository.save(technician));
        teamGroupService.refreshAllGroupCounts();
        return response;
    }

    @Override
    public List<TechnicianResponse> getAvailableTechnicians() {
        List<User> technicians = userRepository.findByRole(Role.TECHNICIAN).stream()
            .filter(user -> user.getStatus() == UserStatus.ACTIVE)
            .toList();

        if (technicians.isEmpty()) {
            throw new RuntimeException("No available technicians found");
        }

        return technicians.stream().map(TechnicianMapper::toResponse).toList();
    }

    @Override
    public void delete(String EID) {
        User technician = userRepository.findByEid(EID)
            .filter(user -> user.getRole() == Role.TECHNICIAN)
            .orElseThrow(() -> new RuntimeException("Technician not found with EID: " + EID));

        userRepository.delete(technician);
        teamGroupService.refreshAllGroupCounts();
    }

    private TeamGroupName resolveTeamGroupName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return TeamGroupName.OTHER;
        }

        try {
            return TeamGroupName.valueOf(rawName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid team group: " + rawName);
        }
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
