package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.UserResponse;
import com.marjane.ems.Entities.User;

/**
 * Mapper class for converting User entities to UserResponse DTOs.
 * Implements Single Responsibility Principle by separating mapping logic.
 */
public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new UserResponse(
            user.getId(),
            user.getEid(),
            user.getLastName(),
            user.getFirstName(),
            user.getEmail(),
            user.getPhone(),
            user.getRole() != null ? user.getRole().name() : null,
            user.getStatus() != null ? user.getStatus().name() : null,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
