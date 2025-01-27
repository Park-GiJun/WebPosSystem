package com.gijun.backend.common.dto.user;

import com.gijun.backend.common.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class SignUpRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Password must be minimum 8 characters, at least one letter and one number")
        private String password;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        private String phoneNumber;
    }

    @Getter
    @Setter
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter
    @Setter
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Password must be minimum 8 characters, at least one letter and one number")
        private String newPassword;
    }

    @Getter
    @Setter
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String phoneNumber;
        private String role;
        private String lastLoginDate;
        private String lastLoginIp;

        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.role = String.valueOf(user.getRole());
            this.lastLoginDate = user.getLastLoginDate() != null ?
                    user.getLastLoginDate().toString() : null;
            this.lastLoginIp = user.getLastLoginIp();
        }
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String accessToken;
//        private String refreshToken;
        private String username;
        private String email;
        private String role;

        public LoginResponse(String accessToken, User user) {
            this.accessToken = accessToken;
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = String.valueOf(user.getRole());
        }
    }
}