package com.marjane.ems.DTO.request;

public record DepartmentRequest(
    Long departmentId,
    String name,
    String description
) {}
