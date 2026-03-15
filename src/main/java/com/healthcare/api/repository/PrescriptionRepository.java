package com.healthcare.api.repository;

import com.healthcare.api.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
}
