package com.marjane.ems.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.PresenceRepository;
import com.marjane.ems.DAL.ShiftRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.PresenceRequest;
import com.marjane.ems.DTO.response.PresenceResponse;
import com.marjane.ems.Entities.Presence;
import com.marjane.ems.Mapper.PresenceMapper;

@Service
public class PresenceServiceImpl implements PresenceService {

    private final PresenceRepository presenceRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public PresenceServiceImpl(PresenceRepository presenceRepository,
                             UserRepository userRepository,
                             ShiftRepository shiftRepository) {
        this.presenceRepository = presenceRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @Override
    public PresenceResponse createPresence(PresenceRequest request) {
        Presence presence = PresenceMapper.toEntity(request);

        userRepository.findById(request.userId())
            .ifPresentOrElse(
                user -> presence.setUser(user),
                () -> {
                    throw new RuntimeException("User not found with ID: " + request.userId());
                }
            );

        if (request.shiftId() != null) {
            shiftRepository.findById(request.shiftId())
                .ifPresentOrElse(
                    shift -> presence.setShift(shift),
                    () -> {
                        throw new RuntimeException("Shift not found with ID: " + request.shiftId());
                    }
                );
        }

        return PresenceMapper.toResponse(presenceRepository.save(presence));
    }

    @Override
    public Optional<PresenceResponse> getPresenceById(Long id) {
        return presenceRepository.findById(id).map(PresenceMapper::toResponse);
    }

    @Override
    public List<PresenceResponse> getAllPresences() {
        return presenceRepository.findAll().stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getPresencesByUser(Long userId) {
        List<Presence> presences = presenceRepository.findByUserId(userId);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found for user ID: " + userId);
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getPresencesByDate(LocalDate date) {
        List<Presence> presences = presenceRepository.findByPresenceDate(date);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found for date: " + date);
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getPresencesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Presence> presences = presenceRepository.findByPresenceDateBetween(startDate, endDate);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found in the date range");
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getPresencesByStatus(String status) {
        List<Presence> presences = presenceRepository.findByStatusIgnoreCase(status);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found with status: " + status);
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getPresencesByShift(Long shiftId) {
        List<Presence> presences = presenceRepository.findByShiftId(shiftId);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found for shift ID: " + shiftId);
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public List<PresenceResponse> getUserPresenceByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Presence> presences = presenceRepository.findByUserIdAndPresenceDateBetween(userId, startDate, endDate);

        if (presences.isEmpty()) {
            throw new RuntimeException("No presences found for user in the date range");
        }

        return presences.stream().map(PresenceMapper::toResponse).toList();
    }

    @Override
    public PresenceResponse updatePresence(Long id, PresenceRequest request) {
        Presence presence = presenceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presence not found with ID: " + id));

        PresenceMapper.updateEntity(presence, request);

        if (request.shiftId() != null) {
            shiftRepository.findById(request.shiftId())
                .ifPresentOrElse(
                    shift -> presence.setShift(shift),
                    () -> {
                        throw new RuntimeException("Shift not found with ID: " + request.shiftId());
                    }
                );
        }

        return PresenceMapper.toResponse(presenceRepository.save(presence));
    }

    @Override
    public void deletePresence(Long id) {
        if (!presenceRepository.existsById(id)) {
            throw new RuntimeException("Presence not found with ID: " + id);
        }
        presenceRepository.deleteById(id);
    }
}
