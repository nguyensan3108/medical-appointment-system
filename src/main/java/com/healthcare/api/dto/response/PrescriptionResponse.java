package com.healthcare.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponse {
    private String id;
    private String medicationName;
    private String dosage;
    private String duration;
    private String instructions;
}
