package com.healthcare.api.controller;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<AppointmentResponse> bookAppointment(@RequestBody @Valid AppointmentCreationRequest request){
        return ApiResponse.<AppointmentResponse>builder()
                .code(1000)
                .message("Appointment booked successfully")
                .result(appointmentService.bookAppointment(request))
                .build();
    }
}
