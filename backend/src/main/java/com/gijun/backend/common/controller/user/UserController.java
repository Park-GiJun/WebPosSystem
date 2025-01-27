package com.gijun.backend.common.controller.user;

import com.gijun.backend.common.dto.CommonRequestDto;
import com.gijun.backend.common.dto.CommonResponseDto;
import com.gijun.backend.common.dto.ErrorCode;
import com.gijun.backend.common.dto.user.UserDto;
import com.gijun.backend.common.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = UserDto.LoginResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "로그인 실패",
                            content = @Content(schema = @Schema(implementation = CommonResponseDto.class))
                    )
            }
    )
    @PostMapping("/auth/login")
    public ResponseEntity<CommonResponseDto<UserDto.LoginResponse>> login(
            @Valid @RequestBody CommonRequestDto<UserDto.LoginRequest> request) {
        try {
            UserDto.LoginResponse response = userService.login(request.getData());
            return ResponseEntity.ok(CommonResponseDto.success(response, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponseDto.error(
                    ErrorCode.INVALID_CREDENTIALS.getCode(),
                    e.getMessage()));
        }
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/auth/signup")
    public ResponseEntity<CommonResponseDto<UserDto.UserResponse>> signup(
            @Valid @RequestBody CommonRequestDto<UserDto.SignUpRequest> request) {
        try {
            UserDto.UserResponse response = userService.signup(request.getData());
            return ResponseEntity.ok(CommonResponseDto.success(response, "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponseDto.error(
                    ErrorCode.INVALID_REQUEST.getCode(),
                    e.getMessage()));
        }
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @PostMapping("/auth/logout")
    public ResponseEntity<CommonResponseDto<Void>> logout(Authentication authentication) {
        try {
            userService.logout(authentication.getName());
            return ResponseEntity.ok(CommonResponseDto.success(null, "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponseDto.error(
                    ErrorCode.INVALID_REQUEST.getCode(),
                    e.getMessage()));
        }
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
    @PostMapping("/user/password")
    public ResponseEntity<CommonResponseDto<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody CommonRequestDto<UserDto.ChangePasswordRequest> request) {
        try {
            userService.changePassword(authentication.getName(), request.getData());
            return ResponseEntity.ok(CommonResponseDto.success(null, "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponseDto.error(
                    ErrorCode.INVALID_REQUEST.getCode(),
                    e.getMessage()));
        }
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @GetMapping("/user/me")
    public ResponseEntity<CommonResponseDto<UserDto.UserResponse>> getMyInfo(Authentication authentication) {
        try {
            UserDto.UserResponse response = userService.getUserInfo(authentication.getName());
            return ResponseEntity.ok(CommonResponseDto.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponseDto.error(
                    ErrorCode.INVALID_REQUEST.getCode(),
                    e.getMessage()));
        }
    }
}