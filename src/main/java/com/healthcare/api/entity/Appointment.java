package com.healthcare.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE appointments SET deleted = true WHERE id=?")
public class Appointment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Override
    public boolean equals(Object o){
        if(this== o) return true;
        if(!(o instanceof Appointment appointment)) return false;
        return id != null && id.equals(appointment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
