package com.healthcare.api.validator;

import com.healthcare.api.dto.request.ScheduleCreationRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeRangeValidatorTest {
    private TimeRangeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new TimeRangeValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_StartTimeBeforeEndTime_ReturnsTrue() {
        LocalDateTime baseTime = LocalDateTime.now().plusDays(1);
        ScheduleCreationRequest request = new ScheduleCreationRequest();

        request.setAvailableFrom(baseTime.withHour(8).withMinute(0));
        request.setAvailableTo(baseTime.withHour(9).withMinute(0));

        boolean result = validator.isValid(request, context);

        assertTrue(result);
    }

    @Test
    void isValid_StartTimeAfterEndTime_ReturnsFalse() {
        LocalDateTime baseTime = LocalDateTime.now().plusDays(1);
        ScheduleCreationRequest request = new ScheduleCreationRequest();

        request.setAvailableFrom(baseTime.withHour(10).withMinute(0));
        request.setAvailableTo(baseTime.withHour(9).withMinute(0));

        boolean result = validator.isValid(request, context);

        assertFalse(result);
    }

    @Test
    void isValid_SameTime_ReturnsFalse() {
        LocalDateTime baseTime = LocalDateTime.now();
        ScheduleCreationRequest request = new ScheduleCreationRequest();

        request.setAvailableFrom(baseTime.withHour(9).withMinute(0));
        request.setAvailableTo(baseTime.withHour(9).withMinute(0));

        boolean result = validator.isValid(request, context);

        assertFalse(result);
    }

    @Test
    void isValid_NullTime_ReturnsTrue() {
        LocalDateTime baseTime = LocalDateTime.now();
        ScheduleCreationRequest request = new ScheduleCreationRequest();

        request.setAvailableFrom(null);
        request.setAvailableTo(baseTime.withHour(9).withMinute(0));

        boolean result = validator.isValid(request, context);

        assertTrue(result);
    }
}