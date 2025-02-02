package com.gijun.backend.common.controller;

import com.gijun.backend.common.dto.CommonResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/health")
    public ResponseEntity<CommonResponseDto<String>> healthCheck() {
        return ResponseEntity.ok(CommonResponseDto.success("OK", "Server is running"));
    }
}
