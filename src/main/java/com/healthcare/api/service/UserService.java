package com.healthcare.api.service;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    UserResponse getMyInfo();

    PageResponse<UserResponse> getUsers(int page, int size);
    UserResponse getUser(String id);

    UserResponse updateUser(String id, UserUpdateRequest request);

    void deleteUser(String id);
}
