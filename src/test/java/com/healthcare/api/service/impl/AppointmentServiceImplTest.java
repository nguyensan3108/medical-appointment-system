package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.AppointmentCreationRequest;
import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.entity.*;
import com.healthcare.api.event.AppointmentBookedEvent;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.AppointmentMapper;
import com.healthcare.api.repository.*;
import com.healthcare.api.utils.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private DoctorRepository doctorRepository;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private String appointmentId;
    private AppointmentCreationRequest request;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID().toString();
        request = new AppointmentCreationRequest();
    }

    @Test
    void bookAppointment_Success_ReturnAppointmentResponse(){
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Schedule schedule = new Schedule();
        schedule.setId(UUID.fromString(appointmentId));
        schedule.setDoctor(doctor);
        schedule.setAvailable(true);
        schedule.setAvailableFrom(LocalDateTime.now());

        Appointment savedAppointment = new Appointment();
        savedAppointment.setSchedule(schedule);
        AppointmentResponse expectedResponse = new AppointmentResponse();
        request.setScheduleId(schedule.getId());

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(patient));
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(schedule));
        when(appointmentRepository.save(any())).thenReturn(savedAppointment);
        when(appointmentMapper.toAppointmentResponse(any())).thenReturn(expectedResponse);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user1.getEmail());
            var result = appointmentService.bookAppointment(request);

            assertEquals(expectedResponse, result);
            assertFalse(schedule.isAvailable());

            verify(scheduleRepository, times(1)).save(schedule);
            verify(appointmentRepository, times(1)).save(any(Appointment.class));
            verify(eventPublisher, times(1)).publishEvent(any(AppointmentBookedEvent.class));
        }
    }

    @Test
    void bookAppointment_ScheduleAlreadyBooked(){
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Schedule schedule = new Schedule();
        schedule.setId(UUID.fromString(appointmentId));
        schedule.setAvailable(false);

        request.setScheduleId(schedule.getId());

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(patient));
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(schedule));

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user1.getEmail());

            AppException exception = assertThrows(AppException.class, () -> appointmentService.bookAppointment(request));

            assertEquals(ErrorCode.SCHEDULE_ALREADY_BOOKED, exception.getErrorCode());
            verify(appointmentRepository, never()).save(any());
        }
    }

    @Test
    void cancelAppointment_InvalidStatus(){
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.fromString(appointmentId));
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user1.getEmail());

            AppException exception = assertThrows(AppException.class, () -> appointmentService.cancelAppointment(appointmentId));

            assertEquals(ErrorCode.INVALID_STATUS, exception.getErrorCode());
            verify(appointmentRepository, never()).save(any());
        }
    }

    @Test
    void cancelAppointment_Success(){
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Schedule schedule = new Schedule();
        schedule.setAvailable(false);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSchedule(schedule);
        appointment.setStatus(AppointmentStatus.PENDING);

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user1.getEmail());

            appointmentService.cancelAppointment(appointmentId);

            assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
            assertTrue(schedule.isAvailable());

            verify(appointmentRepository, times(1)).save(appointment);
            verify(scheduleRepository, times(1)).save(schedule);
        }
    }

    @Test
    void cancelAppointment_Unauthorized() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn("anonymous@gmail.com");

            AppException exception = assertThrows(AppException.class, () -> appointmentService.cancelAppointment(appointmentId));

            assertEquals(ErrorCode.UNAUTHORIZED_ACTION, exception.getErrorCode());

            verify(appointmentRepository, never()).save(any());
            verify(scheduleRepository, never()).save(any());
        }
    }

    @Test
    void bookAppointment_PatientNotFound() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("patient@gmail.com");


        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user1));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.empty());
        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user1.getEmail());

            AppException exception = assertThrows(AppException.class, () -> appointmentService.bookAppointment(request));

            assertEquals(ErrorCode.PATIENT_NOT_FOUND, exception.getErrorCode());
            verify(appointmentRepository, never()).save(any());
            verify(scheduleRepository, never()).save(any());
        }
    }

    @Test
    void getMyAppointments_Doctor_Success(){
        Role role = new Role();
        role.setName("DOCTOR");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("doctor@gmail.com");
        user.setRole(role);

        Doctor doctor = new Doctor();
        doctor.setId(user.getId());
        doctor.setUser(user);

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Appointment> listAppointments = new ArrayList<>();
        listAppointments.add(new Appointment());
        PageImpl<Appointment> appointmentPage = new PageImpl<>(listAppointments, pageable, 1);

        AppointmentResponse expectedResponse = new AppointmentResponse();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(doctorRepository.findByUserId(any())).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorId(eq(doctor.getId()),any())).thenReturn(appointmentPage);
        when(appointmentMapper.toAppointmentResponse(any())).thenReturn(expectedResponse);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user.getEmail());
            var result = appointmentService.getMyAppointments(page, size);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getData().size());
            assertEquals(expectedResponse, result.getData().get(0));

            verify(userRepository, times(1)).findByEmail(any());
            verify(appointmentRepository, times(1)).findByDoctorId(eq(doctor.getId()),any());
        }
    }

    @Test
    void getMyAppointments_Patient_Success(){
        Role role = new Role();
        role.setName("PATIENT");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("patient@gmail.com");
        user.setRole(role);

        Patient patient = new Patient();
        patient.setId(user.getId());
        patient.setUser(user);

        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Appointment> listAppointments = new ArrayList<>();
        listAppointments.add(new Appointment());
        PageImpl<Appointment> appointmentPage = new PageImpl<>(listAppointments,  pageable, 1);

        AppointmentResponse expectedResponse = new AppointmentResponse();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(patientRepository.findByUserId(any())).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientId(eq(patient.getId()),any())).thenReturn(appointmentPage);
        when(appointmentMapper.toAppointmentResponse(any())).thenReturn(expectedResponse);

        try(var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(user.getEmail());
            var result = appointmentService.getMyAppointments(page, size);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getData().size());
            assertEquals(expectedResponse, result.getData().get(0));

            verify(userRepository, times(1)).findByEmail(any());
            verify(appointmentRepository, times(1)).findByPatientId(eq(patient.getId()),any());
        }
    }
}