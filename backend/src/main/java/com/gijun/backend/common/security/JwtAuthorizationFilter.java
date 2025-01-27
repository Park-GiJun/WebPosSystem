package com.gijun.backend.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gijun.backend.common.dto.CommonResponseDto;
import com.gijun.backend.common.dto.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(
            JwtUtil jwtUtil,
            SessionService sessionService,
            UserDetailsService userDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.sessionService = sessionService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String clientIp = request.getHeader("X-Forwarded-For");
                if (clientIp == null) {
                    clientIp = request.getRemoteAddr();
                }

                if (!sessionService.validateSession(username, clientIp)) {
                    throw new RuntimeException("Invalid session");
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            CommonResponseDto.error(
                                    ErrorCode.INVALID_TOKEN.getCode(),
                                    ErrorCode.INVALID_TOKEN.getMessage()
                            )
                    )
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}