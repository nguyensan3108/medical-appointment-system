package com.healthcare.api.service;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    void createSchedule(ScheduleCreationRequest request);

    List<ScheduleResponse> getMySchedules();
}
