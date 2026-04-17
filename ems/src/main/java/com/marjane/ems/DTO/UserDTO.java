package com.marjane.ems.DTO;

import java.time.LocalDateTime;

public record UserDTO(
    Long id,
    String EID,
    String nom,
    String prenom,
    String email,
    String telephone,
    String role,
    String statut,
    LocalDateTime createdAt
) {}
