package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.AuthResponse;
import com.premiumhub.backend.dto.LoginRequest;
import com.premiumhub.backend.entity.SysUser;
import com.premiumhub.backend.repository.UserRepository;
import com.premiumhub.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private AuthService authService;

    @Test
    void login_success() {
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setPasswordHash("hashed");
        user.setRole("ROLE_ADMIN");

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("password123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtUtil.generateAccessToken("admin", "ROLE_ADMIN")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("admin")).thenReturn("refresh-token");

        AuthResponse res = authService.login(req);

        assertThat(res.getAccessToken()).isEqualTo("access-token");
        assertThat(res.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest req = new LoginRequest();
        req.setUsername("nobody");
        req.setPassword("pass");

        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void login_wrongPassword_throws() {
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setPasswordHash("hashed");
        user.setRole("ROLE_ADMIN");

        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrong");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void refresh_invalidToken_throws() {
        when(jwtUtil.isValid("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void refresh_success() {
        SysUser user = new SysUser();
        user.setUsername("admin");
        user.setRole("ROLE_ADMIN");

        when(jwtUtil.isValid("valid-refresh")).thenReturn(true);
        when(jwtUtil.getUsername("valid-refresh")).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("admin", "ROLE_ADMIN")).thenReturn("new-access");

        AuthResponse res = authService.refresh("valid-refresh");

        assertThat(res.getAccessToken()).isEqualTo("new-access");
        assertThat(res.getRefreshToken()).isEqualTo("valid-refresh");
    }
}
