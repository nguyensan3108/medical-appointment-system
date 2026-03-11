package com.healthcare.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AppointmentCreationRequest {
    @NotNull(message = "Please select your appointment time")
    private UUID scheduleId;

    @NotBlank(message = "Please enter the reason for your medical visit")
    private String reason;
}
