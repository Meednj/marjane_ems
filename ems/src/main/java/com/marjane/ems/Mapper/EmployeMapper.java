package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.request.EmployeRequest;
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
                employe.getUpdatedAt());
    }
    
    public static Employe toEntity(EmployeRequest request) {
        if (request == null) return null;

        Employe employe = new Employe();
        // Base User fields
        employe.setLastName(request.lastName());
        employe.setFirstName(request.firstName());
        employe.setEmail(request.email());
        employe.setPhone(request.phone());
        employe.setStatus(request.status());

        employe.setDepartement(request.departement());
        employe.setActivityStatus(request.activityStatus());
        
        return employe;
    }

    public static void updateEntity(Employe entity, EmployeRequest request) {
        if (request == null || entity == null) return;

        entity.setLastName(request.lastName());
        entity.setFirstName(request.firstName());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status());
        entity.setDepartement(request.departement());
        entity.setActivityStatus(request.activityStatus());
    }
}
