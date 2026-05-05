package com.healthcare.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User existed",  HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1002, "Role not found",  HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1004, "Unauthorized, you don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    PATIENT_NOT_FOUND(1006, "Patient not found", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(1007, "Schedule not found", HttpStatus.NOT_FOUND),
    SCHEDULE_ALREADY_BOOKED(1008, "Schedule already booked", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACTION(1009, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    APPOINTMENT_NOT_FOUND(1010, "Appointment not found", HttpStatus.NOT_FOUND),
    INVALID_STATUS(1011, "Invalid status", HttpStatus.BAD_REQUEST),
    RECORD_ALREADY_EXISTS(1012, "Medical record already exists for this appointment", HttpStatus.BAD_REQUEST),
    RECORD_NOT_FOUND(1013, "Medical record not found for this appointment", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(1014, "Invalid input data", HttpStatus.BAD_REQUEST),
    OPTIMISTIC_LOCK_EXCEPTION(1015, "The data has been updated by someone else", HttpStatus.CONFLICT),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
