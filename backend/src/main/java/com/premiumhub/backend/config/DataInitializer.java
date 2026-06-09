package com.premiumhub.backend.config;

import com.premiumhub.backend.entity.SysUser;
import com.premiumhub.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                SysUser admin = new SysUser();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("password123"));
                admin.setRole("ADMIN");
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);

                SysUser user = new SysUser();
                user.setUsername("user");
                user.setPasswordHash(passwordEncoder.encode("password123"));
                user.setRole("USER");
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);

                System.out.println("✅ 測試帳號初始化完成");
            }
        };
    }
}
