package com.healthcare.api.service;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.dto.response.PageResponse;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentCreationRequest request);
    void cancelAppointment(String appointmentId);
    PageResponse<AppointmentResponse> getMyAppointments(int page, int size);
}
