package com.healthcare.api.controller;

import com.healthcare.api.constant.SuccessCode;
import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.ApiResponse;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.entity.Schedule;
import com.healthcare.api.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<String> createSchedule(@RequestBody @Valid ScheduleCreationRequest request) {
        scheduleService.createSchedule(request);
        return ApiResponse.<String>builder()
                .code(SuccessCode.CREATED.getCode())
                .message("Schedule created successfully")
                .result("Time slots saved to the system")
                .build();
    }

    @GetMapping("/my-schedules")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PageResponse<ScheduleResponse>> getMySchedules(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ScheduleResponse>>builder()
                .code(SuccessCode.DATA_FETCHED.getCode())
                .message("Your work schedule list")
                .result(scheduleService.getMySchedules(page, size))
                .build();
    }
}
