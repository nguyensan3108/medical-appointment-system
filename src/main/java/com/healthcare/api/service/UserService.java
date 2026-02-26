package com.healthcare.api.service;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
}
