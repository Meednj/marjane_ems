package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.AvailabilityRequest;
import com.marjane.ems.DTO.response.AvailabilityResponse;
import java.util.List;
import java.util.Optional;

public interface AvailabilityService {
    AvailabilityResponse createAvailability(AvailabilityRequest request);
    Optional<AvailabilityResponse> getAvailabilityById(Long id);
    Optional<AvailabilityResponse> getAvailabilityByUser(Long userId);
    List<AvailabilityResponse> getAllAvailabilities();
    List<AvailabilityResponse> getAvailabilitiesByStatus(String status);
    AvailabilityResponse updateAvailability(Long id, AvailabilityRequest request);
    AvailabilityResponse updateUserAvailabilityStatus(Long userId, String status);
    void deleteAvailability(Long id);
}
