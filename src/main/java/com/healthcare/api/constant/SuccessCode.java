package com.healthcare.api.constant;

import lombok.Getter;

@Getter
public enum SuccessCode {
    SUCCESS(0, "Operation performed successfully"),
    DATA_FETCHED(0, "Data retrieved successfully"),
    CREATED(201, "Resource created successfully"),
    ;

    private final int code;
    private final String message;

    SuccessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
