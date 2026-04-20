package com.marjane.ems.Services;

import com.marjane.ems.DTO.request.UserRequest;
import com.marjane.ems.DTO.response.UserResponse;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponse createUser(UserRequest request);
    Optional<UserResponse> getUserById(Long id);
    Optional<UserResponse> getUserByEmail(String email);
    Optional<UserResponse> getUserByEID(String EID);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
