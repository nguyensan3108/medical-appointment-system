package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.entity.Doctor;
import com.healthcare.api.entity.Schedule;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.ScheduleMapper;
import com.healthcare.api.repository.DoctorRepository;
import com.healthcare.api.repository.ScheduleRepository;
import com.healthcare.api.repository.UserRepository;
import com.healthcare.api.service.ScheduleService;
import com.healthcare.api.utils.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public void createSchedule(ScheduleCreationRequest request) {
        Doctor doctor = getCurrentDoctor();

        LocalDateTime currentSlot = request.getAvailableFrom();
        LocalDateTime endTime = request.getAvailableTo();
        int duration = request.getDurationTime();

        List<Schedule> existingSchedules = scheduleRepository.findByDoctorIdAndAvailableFromBetween(
                doctor.getId(),
                currentSlot,
                endTime
        );

        Set<LocalDateTime> existingSlots = existingSchedules.stream()
                .map(Schedule::getAvailableFrom)
                .collect(Collectors.toSet());

        List<Schedule> schedulesToSave = new ArrayList<>();

        while (currentSlot.plusMinutes(duration).isBefore(endTime) ||
                currentSlot.plusMinutes(duration).equals(endTime)) {
            LocalDateTime nextSlot = currentSlot.plusMinutes(duration);

            boolean exists = existingSlots.contains(currentSlot);

            if (!exists) {
                Schedule schedule = new Schedule();
                schedule.setDoctor(doctor);
                schedule.setAvailableFrom(currentSlot);
                schedule.setAvailableTo(nextSlot);
                schedule.setAvailable(true);

                schedulesToSave.add(schedule);
            }
            currentSlot = nextSlot;
        }
        if (schedulesToSave.isEmpty()) {
            throw new AppException(ErrorCode.SCHEDULE_ALREADY_BOOKED);
        }
        scheduleRepository.saveAll(schedulesToSave);
    }

    @Override
    public List<ScheduleResponse> getMySchedules() {
        Doctor doctor = getCurrentDoctor();
        List<Schedule> schedules = scheduleRepository.findByDoctorIdAndAvailableFromBetween(
                doctor.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3)
        );
        return schedules.stream().map(scheduleMapper::toScheduleResponse).toList();
    }

    private Doctor getCurrentDoctor() {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
