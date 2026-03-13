package com.healthcare.api.mapper;

import com.healthcare.api.dto.response.ScheduleResponse;
import com.healthcare.api.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "doctor.id", target = "doctorId")
    ScheduleResponse toScheduleResponse(Schedule schedule);
}
