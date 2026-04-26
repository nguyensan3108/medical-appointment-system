package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<AppointmentResponse> bookAppointment(@RequestBody @Valid AppointmentCreationRequest request){
        return ApiResponse.<AppointmentResponse>builder()
                .code(SuccessCode.CREATED.getCode())
                .message("Appointment booked successfully")
                .result(appointmentService.bookAppointment(request))
                .build();
    }

    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ApiResponse<String> cancelAppointment(@PathVariable String appointmentId){
        appointmentService.cancelAppointment(appointmentId);
        return ApiResponse.<String>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message("Appointment has been cancelled successfully")
                .build();
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ApiResponse<PageResponse<AppointmentResponse>> getMyAppointments(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return ApiResponse.<PageResponse<AppointmentResponse>>builder()
                .code(SuccessCode.SUCCESS.getCode())
                .message("Fetched appointments successfully")
                .result(appointmentService.getMyAppointments(page, size))
                .build();
    }
}
