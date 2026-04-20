package com.marjane.ems.Mapper;

import com.marjane.ems.DTO.response.SimpleUserResponse;
import com.marjane.ems.Entities.User;

/**
 * Mapper class for User entity to SimpleUserResponse DTO.
 * Used for nested relationships to avoid circular references.
 */
public class SimpleUserMapper {

    /**
     * Converts a User entity to a SimpleUserResponse DTO.
     */
    public static SimpleUserResponse toResponse(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return new SimpleUserResponse(
            user.getId(),
            user.getEID(),
            user.getLastName(),
            user.getFirstName()
        );
    }
}
