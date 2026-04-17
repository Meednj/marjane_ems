package com.marjane.ems.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record EmployeDTO(
    Long id,
    String EID,
    String nom,
    String prenom,
    String email,
    String telephone,
    String departement,
    String statutActivite,
    String role,
    String statut,
    LocalDateTime createdAt,
    List<Long> ticketIds
) {}