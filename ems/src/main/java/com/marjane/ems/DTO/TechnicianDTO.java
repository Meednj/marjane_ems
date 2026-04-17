package com.marjane.ems.DTO;

import java.util.List;

public record TechnicianDTO(
    Long id,
    String EID,
    String nom,
    String prenom,
    String email,
    List<Long> assignedTicketIds 
) {}