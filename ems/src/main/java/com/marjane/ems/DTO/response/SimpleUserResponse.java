package com.marjane.ems.DTO.response;

/**
 * Minimal user representation for nested relationships.
 * Avoids circular references and deep nesting.
 */
public record SimpleUserResponse(
    Long id,
    String EID,
    String lastName,
    String firstName
) {}
