package com.healthcare.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCreationRequest {
    @NotBlank(message = "Email address can not be left blank")
    @Email(message = "Email is not in the correct format")
    private String email;

    @Size(min = 8, message = "Password must have at least 8 characters")
    private String password;

    @NotBlank(message = "Name cannot be left blank")
    private String fullName;

    private String phone;
    private Integer roleId;

    private String specialization;
    private Integer experienceYears;
    private String biography;

    private String bloodType;
    private BigDecimal height;
    private BigDecimal weight;
    private String medicalHistory;
}
