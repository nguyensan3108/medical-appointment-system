package com.healthcare.api.repository;

import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);
}
