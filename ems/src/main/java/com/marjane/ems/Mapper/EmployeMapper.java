package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.EmployeResponse;
import com.marjane.ems.Entities.Employe;

/**
 * Mapper class for Employe entity to EmployeResponse DTO.
 */
public class EmployeMapper {

    /**
     * Converts a Employe entity to an EmployeResponse DTO.
     */
    public static EmployeResponse toResponse(Employe employe) {
        if (employe == null) {
            throw new IllegalArgumentException("Employe cannot be null");
        }

        return new EmployeResponse(
            employe.getId(),
            employe.getEID(),
            employe.getLastName(),
            employe.getFirstName(),
            employe.getEmail(),
            employe.getPhone(),
            employe.getRole(),
            employe.getStatus(),
            employe.getDepartement(),
            employe.getActivityStatus(),
            employe.getCreatedAt(),
            employe.getUpdatedAt()
        );
    }
}
