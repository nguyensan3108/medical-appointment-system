package com.healthcare.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE prescriptions SET deleted = true WHERE id=?")
public class Prescription extends  BaseEntity {
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

    @Override
    public boolean equals(Object o){
        if(this== o) return true;
        if(!(o instanceof Prescription prescription)) return false;
        return id != null && id.equals(prescription.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
