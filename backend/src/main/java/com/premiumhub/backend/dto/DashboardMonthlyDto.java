package com.premiumhub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardMonthlyDto {
    private String month;        // "2026-01"
    private Long count;
    private java.math.BigDecimal amount;
}
