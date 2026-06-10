package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.DashboardMonthlyDto;
import com.premiumhub.backend.dto.DashboardProjection;
import com.premiumhub.backend.dto.DashboardResponse;
import com.premiumhub.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        DashboardProjection stats = paymentRepository.findDashboardStats();

        List<DashboardMonthlyDto> trend = paymentRepository.findMonthlyTrendRaw()
                .stream()
                .map(row -> new DashboardMonthlyDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        new BigDecimal(row[2].toString())
                ))
                .toList();

        return DashboardResponse.builder()
                .totalPayments(stats.getTotalPayments())
                .totalAmount(stats.getTotalAmount())
                .successCount(stats.getSuccessCount())
                .pendingCount(stats.getPendingCount())
                .monthlyTrend(trend)
                .build();
    }
}
