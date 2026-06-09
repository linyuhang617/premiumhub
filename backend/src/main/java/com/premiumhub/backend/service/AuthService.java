package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.AuthResponse;
import com.premiumhub.backend.dto.LoginRequest;
import com.premiumhub.backend.entity.SysUser;
import com.premiumhub.backend.repository.UserRepository;
import com.premiumhub.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        SysUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("帳號不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密碼錯誤");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isValid(refreshToken)) {
            throw new RuntimeException("Refresh Token 無效或已過期");
        }
        String username = jwtUtil.getUsername(refreshToken);
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("帳號不存在"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        return new AuthResponse(newAccessToken, refreshToken);
    }
}
