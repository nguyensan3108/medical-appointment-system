package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.response.MedicalRecordResponse;
import com.healthcare.api.entity.Appointment;
import com.healthcare.api.entity.AppointmentStatus;
import com.healthcare.api.entity.MedicalRecord;
import com.healthcare.api.entity.Prescription;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.MedicalRecordMapper;
import com.healthcare.api.repository.AppointmentRepository;
import com.healthcare.api.repository.MedicalRecordRepository;
import com.healthcare.api.service.MedicalRecordService;
import com.healthcare.api.utils.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    @Transactional
    public MedicalRecordResponse createMedicalRecord(MedicalRecordCreationRequest request){
        Appointment appointment = appointmentRepository.findById(UUID.fromString(request.getAppointmentId()))
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if(medicalRecordRepository.findByAppointmentId(appointment.getId()).isPresent()){
            throw new AppException(ErrorCode.RECORD_ALREADY_EXISTS);
        }

        if(appointment.getStatus().equals(AppointmentStatus.CANCELLED)){
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        MedicalRecord medicalRecord = medicalRecordMapper.toMedicalRecord(request);
        medicalRecord.setAppointment(appointment);

        if(request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()){
            List<Prescription> prescriptions = request.getPrescriptions().stream()
                    .map(pReq -> {
                        Prescription prescription = medicalRecordMapper.toPrescription(pReq);
                        prescription.setMedicalRecord(medicalRecord);
                        return prescription;
                    })
                    .collect(Collectors.toList());
            medicalRecord.setPrescriptions(prescriptions);
        }

        MedicalRecord saveRecord = medicalRecordRepository.save(medicalRecord);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return medicalRecordMapper.toMedicalRecordResponse(saveRecord);
    }

    @Override
    public MedicalRecordResponse getMedicalRecordByAppointmentId(String appointmentId){
        MedicalRecord record = medicalRecordRepository.findByAppointmentId(UUID.fromString(appointmentId))
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        String patientEmail = record.getAppointment().getPatient().getUser().getEmail();
        String doctorEmail = record.getAppointment().getDoctor().getUser().getEmail();

        if(!currentUserEmail.equals(patientEmail) && !currentUserEmail.equals(doctorEmail)){
            throw new AppException(ErrorCode.UNAUTHORIZED_ACTION);
        }
        return medicalRecordMapper.toMedicalRecordResponse(record);
    }

}
