package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        UserResponse result = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .code(SuccessCode.CREATED.getCode())
                .message("User created successfully")
                .result(result)
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(SuccessCode.DATA_FETCHED.getCode())
                .message("Get all users")
                .result(userService.getUsers(page, size))
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return  ApiResponse.<UserResponse>builder()
                .code(SuccessCode.DATA_FETCHED.getCode())
                .message("Get user information by id")
                .result(userService.getUser(userId))
                .build();
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ApiResponse<UserResponse> updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message("Update user information by id")
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message("Delete user information by id")
                .result("User Id deleted: " + userId)
                .build();
    }

}
