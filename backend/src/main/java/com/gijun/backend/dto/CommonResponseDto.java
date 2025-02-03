package com.gijun.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto<T> {
    private final boolean success;
    private final String errorCode;
    private final String message;
    private final T data;

    public static <T> CommonResponseDto<T> success(T data) {
        return success(data, null);
    }

    public static <T> CommonResponseDto<T> success(T data, String message) {
        return CommonResponseDto.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> CommonResponseDto<T> error(String errorCode, String message) {
        return CommonResponseDto.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}