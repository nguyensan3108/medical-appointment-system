package com.healthcare.api.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotPastDateValidatorTest {
    private NotPastDateValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new NotPastDateValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_FutureDate_ReturnsTrue() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        boolean result = validator.isValid(tomorrow, context);
        assertTrue(result);
    }

    @Test
    void isValid_PresentDate_ReturnsTrue() {
        LocalDateTime today = LocalDateTime.now();
        boolean result = validator.isValid(today, context);
        assertTrue(result);
    }

    @Test
    void isValid_PastDate_ReturnsFalse() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        boolean result = validator.isValid(yesterday, context);
        assertFalse(result);
    }

    @Test
    void isValid_NullDate_ReturnsTrue() {
        LocalDateTime time = null;
        boolean result = validator.isValid(time, context);
        assertTrue(result);
    }
}