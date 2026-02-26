package com.healthcare.api.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String status;

    private String roleName;
}
