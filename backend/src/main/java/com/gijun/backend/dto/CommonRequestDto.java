package com.gijun.backend.dto;

import lombok.Getter;

@Getter
public class CommonRequestDto<T> {
    private T data;
}
