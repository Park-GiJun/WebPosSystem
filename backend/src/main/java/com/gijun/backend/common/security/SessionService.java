package com.gijun.backend.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {
    private static final String SESSION_PREFIX = "session:";
    private static final String USER_PREFIX = "user:";
    private static final long SESSION_TIMEOUT = 3L; // 3시간

    private final RedisTemplate<String, Object> redisTemplate;

    public void createSession(String username, String ip, String token) {
        String sessionKey = SESSION_PREFIX + username;
        String userKey = USER_PREFIX + username;

        // 기존 세션이 있다면 삭제
        redisTemplate.delete(sessionKey);

        // 새로운 세션 정보 저장
        Map<String, String> sessionInfo = new HashMap<>();
        sessionInfo.put("token", token);
        sessionInfo.put("ip", ip);
        sessionInfo.put("loginTime", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForValue().set(sessionKey, sessionInfo.toString());
        redisTemplate.expire(sessionKey, SESSION_TIMEOUT, TimeUnit.HOURS);

        // 사용자 정보 업데이트
        redisTemplate.opsForValue().set(userKey, ip);
        redisTemplate.expire(userKey, SESSION_TIMEOUT, TimeUnit.HOURS);
    }

    public boolean validateSession(String username, String ip) {
        String sessionKey = SESSION_PREFIX + username;
        String sessionInfo = (String) redisTemplate.opsForValue().get(sessionKey);

        if (sessionInfo == null) {
            return false;
        }

        return sessionInfo.contains(ip);
    }

    public void removeSession(String username) {
        String sessionKey = SESSION_PREFIX + username;
        String userKey = USER_PREFIX + username;

        redisTemplate.delete(sessionKey);
        redisTemplate.delete(userKey);
    }

    public boolean isUserLoggedIn(String username) {
        String sessionKey = SESSION_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }

    public void refreshSession(String username) {
        String sessionKey = SESSION_PREFIX + username;
        String userKey = USER_PREFIX + username;

        redisTemplate.expire(sessionKey, SESSION_TIMEOUT, TimeUnit.HOURS);
        redisTemplate.expire(userKey, SESSION_TIMEOUT, TimeUnit.HOURS);
    }
}