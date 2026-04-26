package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.entity.*;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.AppointmentMapper;
import com.healthcare.api.repository.*;
import com.healthcare.api.service.AppointmentService;
import com.healthcare.api.utils.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentCreationRequest request){
        String email = SecurityUtils.getCurrentUserEmail();
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

        return appointmentMapper.toAppointmentResponse(savedAppointment);
    }

    @Override
    @Transactional
    public void cancelAppointment(String appointmentId) {
        Appointment appointment = appointmentRepository.findById(UUID.fromString(appointmentId))
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        String patientEmail = appointment.getPatient().getUser().getEmail();
        String doctorEmail = appointment.getDoctor().getUser().getEmail();

        if (!currentUserEmail.equals(patientEmail) && !currentUserEmail.equals(doctorEmail)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        if(appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        Schedule schedule = appointment.getSchedule();
        schedule.setAvailable(true);
        scheduleRepository.save(schedule);
    }

    @Override
    public PageResponse<AppointmentResponse> getMyAppointments(int page, int size) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointmentPage;

        if(user.getRole().getName().toUpperCase().contains("DOCTOR")) {
            Doctor doctor = doctorRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            appointmentPage = appointmentRepository.findByDoctorId(doctor.getId(), pageable);
        } else {
            Patient patient = patientRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            appointmentPage = appointmentRepository.findByPatientId(patient.getId(), pageable);
        }

        List<AppointmentResponse> responses = appointmentPage.getContent().stream()
                .map(appointmentMapper::toAppointmentResponse)
                .toList();

        return PageResponse.<AppointmentResponse>builder()
                .currentPage(page)
                .pageSize(appointmentPage.getSize())
                .totalPages(appointmentPage.getTotalPages())
                .totalElements(appointmentPage.getTotalElements())
                .data(responses)
                .build();
    }
}