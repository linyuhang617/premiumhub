package com.premiumhub.backend.dto;

public interface DashboardProjection {
    Long getTotalPayments();
    java.math.BigDecimal getTotalAmount();
    Long getSuccessCount();
    Long getPendingCount();
}
