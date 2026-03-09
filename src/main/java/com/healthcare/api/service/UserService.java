package com.healthcare.api.service;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    UserResponse getMyInfo();

    List<UserResponse> getUsers();
    UserResponse getUser(String id);

    UserResponse updateUser(String id, UserUpdateRequest request);

    void deleteUser(String id);
}
