package com.premiumhub.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private Long totalPayments;
    private BigDecimal totalAmount;
    private Long successCount;
    private Long pendingCount;
    private List<DashboardMonthlyDto> monthlyTrend;
}
