package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import java.util.List;
import java.util.Optional;

public interface TechnicianService {
    TechnicianResponse createTechnician(TechnicianRequest request);
    Optional<TechnicianResponse> getTechnicianById(Long id);
    Optional<TechnicianResponse> getTechnicianByEID(String EID);
    Optional<TechnicianResponse> getTechnicianByEmail(String email);
    List<TechnicianResponse> getAllTechnicians();
    List<TechnicianResponse> getAvailableTechnicians();
    TechnicianResponse updateTechnician(Long id, TechnicianRequest request);
    void deleteTechnician(Long id);
}
