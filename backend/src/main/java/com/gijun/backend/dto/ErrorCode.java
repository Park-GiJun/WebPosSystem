package com.gijun.backend.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Auth Errors
    INVALID_CREDENTIALS("AUTH_001", "Invalid credentials"),
    USER_NOT_FOUND("AUTH_002", "User not found"),
    TOKEN_EXPIRED("AUTH_003", "Token has expired"),
    INVALID_TOKEN("AUTH_004", "Invalid token"),
    DUPLICATE_LOGIN("AUTH_005", "User already logged in"),

    // User Errors
    USERNAME_ALREADY_EXISTS("USER_001", "Username already exists"),
    EMAIL_ALREADY_EXISTS("USER_002", "Email already exists"),
    INVALID_PASSWORD("USER_003", "Invalid password"),

    // Common Errors
    INVALID_REQUEST("COMMON_001", "Invalid request"),
    INTERNAL_SERVER_ERROR("COMMON_002", "Internal server error"),
    ACCESS_DENIED("COMMON_003", "Access denied");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}