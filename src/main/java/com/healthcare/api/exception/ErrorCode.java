package com.healthcare.api.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định"),
    USER_EXISTED(1001, "Email đã được sử dụng"),
    ROLE_NOT_FOUND(1002, "Không tìm thấy quyền hạn này");

    private  final int code;
    private  final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
