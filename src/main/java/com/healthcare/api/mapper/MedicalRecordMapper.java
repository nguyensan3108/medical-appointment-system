package com.healthcare.api.mapper;

import com.healthcare.api.dto.request.MedicalRecordCreationRequest;
import com.healthcare.api.dto.request.PrescriptionRequest;
import com.healthcare.api.dto.response.MedicalRecordResponse;
import com.healthcare.api.dto.response.PrescriptionResponse;
import com.healthcare.api.entity.MedicalRecord;
import com.healthcare.api.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "appointment",  ignore = true)
    @Mapping(target = "prescriptions",   ignore = true)
    MedicalRecord toMedicalRecord(MedicalRecordCreationRequest request);

    Prescription toPrescription(PrescriptionRequest request);

    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "appointment.patient.user.fullName", target = "patientName")
    @Mapping(source = "appointment.doctor.user.fullName", target = "doctorName")
    MedicalRecordResponse toMedicalRecordResponse(MedicalRecord medicalRecord);

    PrescriptionResponse toPrescriptionResponse(Prescription prescription);
}
