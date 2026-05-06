package com.marjane.ems.DTO.response;

public record DepartmentResponse(
    Long departmentId,
    String name,
    String description
) {}