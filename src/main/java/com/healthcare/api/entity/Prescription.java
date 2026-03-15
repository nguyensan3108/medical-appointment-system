package com.healthcare.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "medication_name",  nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private String dosage;

    private String duration;

    @Column(columnDefinition = "TEXT")
    private String instructions;


}
