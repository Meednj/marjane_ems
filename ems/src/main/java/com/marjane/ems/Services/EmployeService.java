package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import java.util.List;
import java.util.Optional;

public interface EmployeService {
    EmployeResponse createEmploye(EmployeRequest request);
    Optional<EmployeResponse> getEmployeById(Long id);
    Optional<EmployeResponse> getEmployeByEID(String EID);
    Optional<EmployeResponse> getEmployeByEmail(String email);
    List<EmployeResponse> getAllEmployes();
    List<EmployeResponse> getEmployesByDepartement(String departement);
    List<EmployeResponse> getEmployesByActivityStatus(String activityStatus);
    EmployeResponse updateEmploye(Long id, EmployeRequest request);
    void deleteEmploye(Long id);
}
