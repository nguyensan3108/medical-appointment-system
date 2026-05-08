package com.healthcare.api.service;

public interface EmailService {
    void sendAppointmentConfirmation(String toEmail, String patientName, String date, String time);
}
