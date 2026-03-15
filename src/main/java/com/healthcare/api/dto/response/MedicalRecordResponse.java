package com.healthcare.api.dto.response;

import com.healthcare.api.dto.request.PrescriptionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponse {
    private String id;
    private String appointmentId;
    private String patientName;
    private String doctorName;
    private String symptoms;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    private LocalDateTime createdAt;

    private List<PrescriptionResponse> prescriptions;

}
