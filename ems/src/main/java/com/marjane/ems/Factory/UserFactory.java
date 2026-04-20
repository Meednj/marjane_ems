package com.marjane.ems.Factory;

import com.marjane.ems.DTO.request.UserRequest;
import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Employe;
import com.marjane.ems.Entities.Technician;
import com.marjane.ems.Entities.Administrator;

public class UserFactory {

    public User createUser(UserRequest request, String role) {

        return switch (role.toUpperCase()) {

            case "EMPLOYE", "EMPLOYEE" -> createEmploye(request);
            case "ADMINISTRATOR", "ADMIN" -> createAdministrator(request);
            case "TECHNICIAN", "TECH" -> createTechnician(request);

            default -> throw new IllegalArgumentException("Invalid role: " + role);
        };
    }

    private Employe createEmploye(UserRequest request) {
        Employe e = new Employe();
        populateBaseFields(e, request);
        e.setDepartement("UNASSIGNED");
        e.setActivityStatus("ACTIVE");
        return e;
    }

    private Technician createTechnician(UserRequest request) {
        Technician t = new Technician();
        populateBaseFields(t, request);
        return t;
    }

    private Administrator createAdministrator(UserRequest request) {
        Administrator a = new Administrator();
        populateBaseFields(a, request);
        return a;
    }

    private void populateBaseFields(User user, UserRequest request) {
        user.setLastName(request.lastName());
        user.setFirstName(request.firstName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(request.status() != null ? request.status() : "ACTIVE");
    }
}