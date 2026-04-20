package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.AdministratorRequest;
import com.marjane.ems.DTO.response.AdministratorResponse;
import java.util.List;
import java.util.Optional;

public interface AdministratorService {
    AdministratorResponse createAdministrator(AdministratorRequest request);
    Optional<AdministratorResponse> getAdministratorById(Long id);
    Optional<AdministratorResponse> getAdministratorByEID(String EID);
    Optional<AdministratorResponse> getAdministratorByEmail(String email);
    List<AdministratorResponse> getAllAdministrators();
    AdministratorResponse updateAdministrator(Long id, AdministratorRequest request);
    void deleteAdministrator(Long id);
}
