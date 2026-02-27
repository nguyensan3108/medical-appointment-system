package com.healthcare.api.controller;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(){
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Get all users")
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return  ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Get user information by id")
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Update user information by id")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Delete user information by id")
                .result("User Id deleted: " + userId)
                .build();
    }

}
