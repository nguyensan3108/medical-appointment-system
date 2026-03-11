package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.entity.Appointment;
import com.healthcare.api.entity.Patient;
import com.healthcare.api.entity.Schedule;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.repository.AppointmentRepository;
import com.healthcare.api.repository.PatientRepository;
import com.healthcare.api.repository.ScheduleRepository;
import com.healthcare.api.repository.UserRepository;
import com.healthcare.api.service.AppointmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentCreationRequest request){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PATIENT_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.isAvailable()) {
            throw new AppException(ErrorCode.SCHEDULE_ALREADY_BOOKED);
        }

        schedule.setAvailable(false);
        scheduleRepository.save(schedule);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(schedule.getDoctor());
        appointment.setSchedule(schedule);
        appointment.setReason(request.getReason());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToResponse(savedAppointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment){
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setPatientName(appointment.getPatient().getUser().getFullName());
        response.setDoctorName(appointment.getDoctor().getUser().getFullName());
        response.setSpecialization(appointment.getDoctor().getSpecialization());
        response.setStartTime(appointment.getSchedule().getAvailableFrom());
        response.setEndTime(appointment.getSchedule().getAvailableTo());
        response.setReason(appointment.getReason());
        response.setStatus(appointment.getStatus().name());

        return response;
    }
}
