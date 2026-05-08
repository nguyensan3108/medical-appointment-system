package com.healthcare.api.event;

public record AppointmentBookedEvent(
        String email,
        String patientName,
        String date,
        String time
) { }
