package com.premiumhub.backend.config;

import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.entity.SysUser;
import com.premiumhub.backend.repository.PolicyRepository;
import com.premiumhub.backend.repository.UserRepository;
import com.premiumhub.backend.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesUtil aesUtil;

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

            if (policyRepository.count() == 0) {
                Object[][] data = {
                    {"POL-2024-001", "王大明", "A123456789", "12000", "ACTIVE",   "2026-12-31"},
                    {"POL-2024-002", "李小花", "B234567890", "8500",  "ACTIVE",   "2026-08-15"},
                    {"POL-2024-003", "張志偉", "C345678901", "15000", "EXPIRED",  "2025-03-01"},
                    {"POL-2024-004", "陳美玲", "D456789012", "9800",  "ACTIVE",   "2027-01-20"},
                    {"POL-2024-005", "林俊宏", "E567890123", "20000", "CANCELLED","2025-06-30"},
                    {"POL-2024-006", "黃雅婷", "F678901234", "11500", "ACTIVE",   "2026-10-05"},
                    {"POL-2024-007", "吳建志", "G789012345", "7200",  "EXPIRED",  "2025-01-15"},
                    {"POL-2024-008", "鄭淑芬", "H890123456", "18000", "ACTIVE",   "2027-05-22"},
                };

                for (Object[] row : data) {
                    Policy p = new Policy();
                    p.setPolicyNo((String) row[0]);
                    p.setInsuredName(aesUtil.encrypt((String) row[1]));
                    p.setIdNoEncrypted(aesUtil.encrypt((String) row[2]));
                    p.setPremiumAmount(new BigDecimal((String) row[3]));
                    p.setStatus((String) row[4]);
                    p.setDueDate(LocalDate.parse((String) row[5]));
                    policyRepository.save(p);
                }

                System.out.println("✅ 測試保單初始化完成（8 筆）");
            }
        };
    }
}
