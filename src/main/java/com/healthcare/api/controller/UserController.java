package com.healthcare.api.controller;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        UserResponse result = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("User created successfully")
                .result(result)
                .build();
    }
}
