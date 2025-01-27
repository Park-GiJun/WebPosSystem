package com.gijun.backend.common.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String username;
    private String role;
}