package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.TechnicianRequest;
import com.marjane.ems.DTO.response.TechnicianResponse;
import java.util.List;

public interface TechnicianService extends BaseUserService<TechnicianRequest, TechnicianResponse> {
    List<TechnicianResponse> getAvailableTechnicians();
}