package com.gijun.backend.common.service.user;

import com.gijun.backend.common.domain.user.User;
import com.gijun.backend.common.dto.ErrorCode;
import com.gijun.backend.common.dto.user.UserDto;
import com.gijun.backend.common.repository.user.UserRepository;
import com.gijun.backend.common.security.JwtUtil;
import com.gijun.backend.common.security.SessionService;
import com.gijun.backend.common.service.KafkaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final KafkaService kafkaService;

    @Transactional
    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        if (sessionService.isUserLoggedIn(request.getUsername())) {
            throw new RuntimeException(ErrorCode.DUPLICATE_LOGIN.getMessage());
        }

        HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = servletRequest.getRemoteAddr();
        }

        String token = jwtUtil.generateToken(user.getUsername(), clientIp);
        sessionService.createSession(user.getUsername(), clientIp, token);

        user.updateLastLogin(clientIp);
        userRepository.save(user);

        kafkaService.sendAuthenticationEvent("User logged in: " + user.getUsername());

        return new UserDto.LoginResponse(
                token,
                user
        );
    }

    @Transactional
    public UserDto.UserResponse signup(UserDto.SignUpRequest request) {
        // 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        kafkaService.sendAuthenticationEvent("New user registered: " + request.getUsername());

        return new UserDto.UserResponse(savedUser);
    }

    @Transactional
    public void logout(String username) {
        sessionService.removeSession(username);
        kafkaService.sendAuthenticationEvent("User logged out: " + username);
    }

    @Transactional
    public void changePassword(String username, UserDto.ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorCode.INVALID_PASSWORD.getMessage());
        }

        user.updatePassword(request.getNewPassword(), passwordEncoder);
        userRepository.save(user);

        // 비밀번호 변경 후 강제 로그아웃
        sessionService.removeSession(username);
        kafkaService.sendAuthenticationEvent("Password changed for user: " + username);
    }

    @Transactional
    public void updateLoginInfo(String username, String ip) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()));

        user.updateLastLogin(ip);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserDto.UserResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()));

        return new UserDto.UserResponse(user);
    }
}