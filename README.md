# PremiumHub — 保費管理系統

## 3 行啟動指令

```bash
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
# 後端 http://localhost:8080　前端 http://localhost:5173
```

## 測試帳號

| 帳號 | 密碼 | 角色 |
|------|------|------|
| admin | password123 | ADMIN |
| user | password123 | USER |

## JaCoCo 覆蓋率報告

```bash
cd backend && mvn test
open target/site/jacoco/index.html
```

## 面試考點對應表

| 考點 | Slice | 關鍵類別 |
|------|-------|---------|
| JWT + Spring Security Filter Chain | S1 | JwtAuthenticationFilter, JwtUtil |
| BCrypt 單向雜湊 | S1 | AuthService, PasswordConfig |
| MyBatis #{} vs ${} + SQL Injection 防護 | S2 | PolicyMapper.xml |
| AES 個資加密 + 遮罩 | S2 | AesUtil, PolicyService |
| JPA 樂觀鎖 @Version + 409 | S3 | Policy entity, GlobalExceptionHandler |
| @Transactional rollbackFor + 自呼叫陷阱 | S4 | PaymentService |
| REQUIRES_NEW 獨立事務（稽核 Log） | S4 | AuditLogService |
| 悲觀鎖 PESSIMISTIC_WRITE（防重複扣款） | S4 | PolicyRepository |
| MDC Trace ID 全程追蹤 | S4 | PaymentService |
| @PreAuthorize RBAC + @EnableMethodSecurity | S5 | SealAuthController, SecurityConfig |
| JPA N+1 → JOIN FETCH | S5 | SealAuthRepository |
| JasperReport 流式 PDF 輸出（防 OOM） | S6 | ReportService, ReportController |
| Thymeleaf SSR vs Vue CSR | S6 | ThymeleafController |
| JPQL GROUP BY 聚合查詢 | S7 | PaymentRepository, DashboardService |
| JaCoCo Service 層覆蓋率 ≥ 70% | S7 | pom.xml jacoco-maven-plugin |
