package com.healthcare.api.dto.request;

import lombok.Data;

@Data
public class UserCreationRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;

    private Integer roleId;
}
