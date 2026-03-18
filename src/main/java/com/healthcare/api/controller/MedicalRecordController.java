package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.MedicalRecordResponse;
import com.healthcare.api.entity.MedicalRecord;
import com.healthcare.api.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<MedicalRecordResponse> createMedicalRecord(@RequestBody @Valid MedicalRecordCreationRequest request) {
        return ApiResponse.<MedicalRecordResponse>builder()
                .code(SuccessCode.CREATED.getCode())
                .message("Medical record created successfully")
                .result(medicalRecordService.createMedicalRecord(request))
                .build();
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ApiResponse<MedicalRecordResponse> getMedicalRecordByAppointmentId(@PathVariable String appointmentId) {
        return ApiResponse.<MedicalRecordResponse>builder()
                .code(SuccessCode.DATA_FETCHED.getCode())
                .message("Medical record retrieved successfully")
                .result(medicalRecordService.getMedicalRecordByAppointmentId(appointmentId))
                .build();
    }
}
