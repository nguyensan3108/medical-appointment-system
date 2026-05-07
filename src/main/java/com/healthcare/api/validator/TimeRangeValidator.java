package com.healthcare.api.validator;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator <ValidTimeRange, ScheduleCreationRequest> {
    @Override
    public boolean isValid(ScheduleCreationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if(request.getAvailableFrom()  == null || request.getAvailableTo() == null){
            return true;
        }
        return request.getAvailableFrom().isBefore(request.getAvailableTo());
    }
}
