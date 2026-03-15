package com.healthcare.api.dto.request;

import com.healthcare.api.entity.Prescription;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordCreationRequest {
    @NotBlank(message = "AppointmentId is required")
    private String appointmentId;

    private String symptoms;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String treatmentPlan;
    private String notes;

    private List<PrescriptionRequest> prescriptions;
}
