package com.gijun.backend.common.dto;

import lombok.Getter;

@Getter
public class CommonRequestDto<T> {
    private T data;
}
