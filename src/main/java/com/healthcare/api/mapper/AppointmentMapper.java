package com.healthcare.api.mapper;

import com.healthcare.api.dto.response.AppointmentResponse;
import com.healthcare.api.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(source = "patient.user.fullName", target = "patientName")
    @Mapping(source = "doctor.user.fullName", target = "doctorName")
    @Mapping(source = "doctor.specialization", target = "specialization")
    @Mapping(source = "schedule.availableFrom", target = "startTime")
    @Mapping(source = "schedule.availableTo", target = "endTime")
    AppointmentResponse toAppointmentResponse(Appointment appointment);
}
