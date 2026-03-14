package com.healthcare.api.service;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentCreationRequest request);
    void cancelAppointment(String appointmentId);
}
