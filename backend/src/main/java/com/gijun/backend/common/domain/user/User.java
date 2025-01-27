package com.gijun.backend.common.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Builder
    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = UserRole.USER;
    }

    public void updateLastLogin(String ip) {
        this.lastLoginDate = LocalDateTime.now();
        this.lastLoginIp = ip;
    }

    public void updatePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }
}