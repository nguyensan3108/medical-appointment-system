package com.healthcare.api.service;

import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.response.MedicalRecordResponse;

public interface MedicalRecordService {
    MedicalRecordResponse createMedicalRecord(MedicalRecordCreationRequest request);
    MedicalRecordResponse getMedicalRecordByAppointmentId(String appointmentId);
}
