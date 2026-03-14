package com.healthcare.api.mapper;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Doctor toDoctor(UserCreationRequest request);
}
