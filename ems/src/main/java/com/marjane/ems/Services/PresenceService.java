package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.PresenceRequest;
import com.marjane.ems.DTO.response.PresenceResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenceService {
    PresenceResponse createPresence(PresenceRequest request);
    Optional<PresenceResponse> getPresenceById(Long id);
    List<PresenceResponse> getAllPresences();
    List<PresenceResponse> getPresencesByUser(Long userId);
    List<PresenceResponse> getPresencesByDate(LocalDate date);
    List<PresenceResponse> getPresencesByDateRange(LocalDate startDate, LocalDate endDate);
    List<PresenceResponse> getPresencesByStatus(String status);
    List<PresenceResponse> getPresencesByShift(Long shiftId);
    List<PresenceResponse> getUserPresenceByDateRange(Long userId, LocalDate startDate, LocalDate endDate);
    PresenceResponse updatePresence(Long id, PresenceRequest request);
    void deletePresence(Long id);
}
