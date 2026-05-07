package com.healthcare.api.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NotPastDateValidator implements ConstraintValidator<NotPastDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        if(localDateTime == null) {
            return true;
        }
        return !localDateTime.toLocalDate().isBefore(java.time.LocalDate.now());
    }
}
