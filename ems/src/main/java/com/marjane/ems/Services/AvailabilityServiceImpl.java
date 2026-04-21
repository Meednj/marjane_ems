package com.marjane.ems.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.AvailabilityRepository;
import com.marjane.ems.DAL.UserRepository;
import com.marjane.ems.DTO.request.AvailabilityRequest;
import com.marjane.ems.DTO.response.AvailabilityResponse;
import com.marjane.ems.Entities.Availability;
import com.marjane.ems.Entities.AvailabilityStatus;
import com.marjane.ems.Mapper.AvailabilityMapper;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                  UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AvailabilityResponse createAvailability(AvailabilityRequest request) {
        Availability availability = AvailabilityMapper.toEntity(request);

        userRepository.findById(request.userId())
            .ifPresentOrElse(
                user -> availability.setUser(user),
                () -> {
                    throw new RuntimeException("User not found with ID: " + request.userId());
                }
            );

        return AvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public Optional<AvailabilityResponse> getAvailabilityById(Long id) {
        return availabilityRepository.findById(id).map(AvailabilityMapper::toResponse);
    }

    @Override
    public Optional<AvailabilityResponse> getAvailabilityByUser(Long userId) {
        return availabilityRepository.findByUserId(userId).map(AvailabilityMapper::toResponse);
    }

    @Override
    public List<AvailabilityResponse> getAllAvailabilities() {
        return availabilityRepository.findAll().stream().map(AvailabilityMapper::toResponse).toList();
    }

    @Override
    public List<AvailabilityResponse> getAvailabilitiesByStatus(String status) {
        List<Availability> availabilities = availabilityRepository.findByStatusIgnoreCase(status);

        if (availabilities.isEmpty()) {
            throw new RuntimeException("No availabilities found with status: " + status);
        }

        return availabilities.stream().map(AvailabilityMapper::toResponse).toList();
    }

    @Override
    public AvailabilityResponse updateAvailability(Long id, AvailabilityRequest request) {
        Availability availability = availabilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Availability not found with ID: " + id));

        AvailabilityMapper.updateEntity(availability, request);
        return AvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public AvailabilityResponse updateUserAvailabilityStatus(Long userId, String status) {
        Availability availability = availabilityRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Availability not found for user ID: " + userId));

        availability.setStatus(AvailabilityStatus.valueOf(status));
        return AvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    public void deleteAvailability(Long id) {
        if (!availabilityRepository.existsById(id)) {
            throw new RuntimeException("Availability not found with ID: " + id);
        }
        availabilityRepository.deleteById(id);
    }
}
