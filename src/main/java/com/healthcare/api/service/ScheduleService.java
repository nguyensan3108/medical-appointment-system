package com.healthcare.api.service;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.ScheduleResponse;

public interface ScheduleService {
    void createSchedule(ScheduleCreationRequest request);

    PageResponse<ScheduleResponse> getMySchedules(int page, int size);
}
