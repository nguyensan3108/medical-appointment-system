package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.request.PrescriptionRequest;
import com.healthcare.api.dto.response.MedicalRecordResponse;
import com.healthcare.api.entity.*;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.MedicalRecordMapper;
import com.healthcare.api.repository.AppointmentRepository;
import com.healthcare.api.repository.MedicalRecordRepository;
import com.healthcare.api.utils.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {
    @Mock
    private MedicalRecordMapper medicalRecordMapper;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordServiceImpl;

    private MedicalRecordCreationRequest request;
    private String appointmentId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID().toString();
        request = new MedicalRecordCreationRequest();
        request.setAppointmentId(appointmentId);
    }


    @Test
    void createMedicalRecord_Success_ReturnMedicalRecordResponse() {
        Appointment appointment = new Appointment();

        MedicalRecordResponse expectedResponse = new MedicalRecordResponse();
        expectedResponse.setAppointmentId(appointmentId);

        MedicalRecord medicalRecord = new MedicalRecord();
        MedicalRecord savedRecord = new MedicalRecord();

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.empty());
        when(medicalRecordMapper.toMedicalRecord(any())).thenReturn(medicalRecord);
        when(medicalRecordRepository.save(any())).thenReturn(savedRecord);
        when(medicalRecordMapper.toMedicalRecordResponse(any())).thenReturn(expectedResponse);

        var result = medicalRecordServiceImpl.createMedicalRecord((request));

        assertEquals(expectedResponse, result);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void getMedicalRecordByAppointmentId_Unauthorized() {
        User user1 = new User();
        user1.setEmail("patient@gmail.com");
        User user2 = new User();
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);

        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.of(medicalRecord));

        try (var mockSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn("stranger@gmail.com");

            AppException exception = assertThrows(AppException.class, () ->
                    medicalRecordServiceImpl.getMedicalRecordByAppointmentId(appointmentId));

            assertEquals(ErrorCode.UNAUTHORIZED_ACTION, exception.getErrorCode());
        }
    }

    @Test
    void createMedicalRecord_AppointmentNotFound(){
        when(appointmentRepository.findById(any())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () ->
                medicalRecordServiceImpl.createMedicalRecord((request)));

        assertEquals(ErrorCode.APPOINTMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createMedicalRecord_RecordAlreadyExists(){
        Appointment appointment = new Appointment();

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.of(new MedicalRecord()));

        AppException exception = assertThrows(AppException.class, () ->
                medicalRecordServiceImpl.createMedicalRecord((request)));
        assertEquals(ErrorCode.RECORD_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void getMedicalRecord_RecordNotFound() {
        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () ->
                medicalRecordServiceImpl.getMedicalRecordByAppointmentId(appointmentId));

        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getMedicalRecord_Success_WhenUserIsPatient() {
        User user1 = new User();
        user1.setEmail("patient@gmail.com");
        User user2 = new User();
        user2.setEmail("doctor@gmail.com");

        Patient patient = new Patient();
        patient.setUser(user1);

        Doctor doctor = new Doctor();
        doctor.setUser(user2);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);

        MedicalRecord record = new MedicalRecord();
        record.setAppointment(appointment);

        MedicalRecordResponse  expectedResponse = new MedicalRecordResponse();
        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.of(record));
        when(medicalRecordMapper.toMedicalRecordResponse(any())).thenReturn(expectedResponse);

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserEmail).thenReturn(patient.getUser().getEmail());

            var result = medicalRecordServiceImpl.getMedicalRecordByAppointmentId(appointmentId);

            assertNotNull(result);
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    void createMedicalRecord_InvalidStatus() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));

        AppException exception = assertThrows(AppException.class, () ->
                medicalRecordServiceImpl.createMedicalRecord((request))
        );

        assertEquals(ErrorCode.INVALID_STATUS, exception.getErrorCode());
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void createMedicalRecord_Success_WithPrescriptions() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);

        MedicalRecordResponse expectedResponse = new MedicalRecordResponse();
        expectedResponse.setAppointmentId(appointmentId);

        List<PrescriptionRequest> listPrescriptions = new ArrayList<>();
        listPrescriptions.add(new PrescriptionRequest());
        request.setPrescriptions(listPrescriptions);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        MedicalRecord savedRecord = new MedicalRecord();

        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.findByAppointmentId(any())).thenReturn(Optional.empty());
        when(medicalRecordMapper.toMedicalRecord(any())).thenReturn(medicalRecord);
        when(medicalRecordRepository.save(any())).thenReturn(savedRecord);
        when(medicalRecordMapper.toMedicalRecordResponse(any())).thenReturn(expectedResponse);
        when(medicalRecordMapper.toPrescription(any())).thenReturn(new Prescription());

        var result = medicalRecordServiceImpl.createMedicalRecord(request);

        assertNotNull(result);
        verify(medicalRecordRepository, times(1)).save(any());
    }
}