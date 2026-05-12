package com.marjane.ems.DTO.response;

import java.util.List;

public record TeamGroupResponse(
    Long id,
    String name,
    Integer numberOfMembers,
    List<TechnicianResponse> technicians
) {}