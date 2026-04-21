package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.EmployeRequest;
import com.marjane.ems.DTO.response.EmployeResponse;
import java.util.List;

public interface EmployeService extends BaseUserService<EmployeRequest, EmployeResponse> {
    List<EmployeResponse> getByDepartement(String departement);
    List<EmployeResponse> getByActivityStatus(String activityStatus);
}