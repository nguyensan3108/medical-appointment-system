package com.healthcare.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "schedules")
@Getter
@Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "available_from",  nullable = false)
    private LocalDateTime availableFrom;

    @Column(name = "available_to",  nullable = false)
    private LocalDateTime availableTo;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o){
        if(this== o) return true;
        if(!(o instanceof Schedule schedule)) return false;
        return id != null && id.equals(schedule.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
