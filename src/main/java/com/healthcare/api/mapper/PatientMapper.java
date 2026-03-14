package com.healthcare.api.mapper;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Patient toPatient(UserCreationRequest request);
}
