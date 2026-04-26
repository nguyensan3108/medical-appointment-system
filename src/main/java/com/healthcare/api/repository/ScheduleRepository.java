package com.healthcare.api.repository;

import com.healthcare.api.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    List<Schedule> findByDoctorIdAndAvailableFromBetween(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime);

    Page<Schedule> findByDoctorIdAndAvailableFromBetween(UUID doctorId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable );
}
