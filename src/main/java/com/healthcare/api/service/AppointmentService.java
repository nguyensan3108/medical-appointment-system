package com.healthcare.api.service;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.entity.Appointment;
import com.healthcare.api.exception.AppException;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentCreationRequest request);
}
