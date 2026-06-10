package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.DashboardProjection;
import com.premiumhub.backend.dto.DashboardResponse;
import com.premiumhub.backend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboard_returnsCorrectStats() {
        DashboardProjection projection = mock(DashboardProjection.class);
        when(projection.getTotalPayments()).thenReturn(5L);
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("50000"));
        when(projection.getSuccessCount()).thenReturn(3L);
        when(projection.getPendingCount()).thenReturn(2L);

        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{"2026-06", 5L, new BigDecimal("50000")});

        when(paymentRepository.findDashboardStats()).thenReturn(projection);
        when(paymentRepository.findMonthlyTrendRaw()).thenReturn(rows);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getTotalPayments()).isEqualTo(5L);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("50000");
        assertThat(result.getSuccessCount()).isEqualTo(3L);
        assertThat(result.getPendingCount()).isEqualTo(2L);
        assertThat(result.getMonthlyTrend()).hasSize(1);
        assertThat(result.getMonthlyTrend().get(0).getMonth()).isEqualTo("2026-06");
    }

    @Test
    void getDashboard_emptyData_returnsZeros() {
        DashboardProjection projection = mock(DashboardProjection.class);
        when(projection.getTotalPayments()).thenReturn(0L);
        when(projection.getTotalAmount()).thenReturn(BigDecimal.ZERO);
        when(projection.getSuccessCount()).thenReturn(0L);
        when(projection.getPendingCount()).thenReturn(0L);

        when(paymentRepository.findDashboardStats()).thenReturn(projection);
        when(paymentRepository.findMonthlyTrendRaw()).thenReturn(new ArrayList<>());

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getTotalPayments()).isZero();
        assertThat(result.getMonthlyTrend()).isEmpty();
    }

    @Test
    void getDashboard_monthlyTrend_correctlyMapped() {
        DashboardProjection projection = mock(DashboardProjection.class);
        when(projection.getTotalPayments()).thenReturn(2L);
        when(projection.getTotalAmount()).thenReturn(new BigDecimal("20000"));
        when(projection.getSuccessCount()).thenReturn(1L);
        when(projection.getPendingCount()).thenReturn(1L);

        List<Object[]> rows = new ArrayList<>();
        rows.add(new Object[]{"2026-05", 1L, new BigDecimal("10000")});
        rows.add(new Object[]{"2026-06", 1L, new BigDecimal("10000")});

        when(paymentRepository.findDashboardStats()).thenReturn(projection);
        when(paymentRepository.findMonthlyTrendRaw()).thenReturn(rows);

        DashboardResponse result = dashboardService.getDashboard();

        assertThat(result.getMonthlyTrend()).hasSize(2);
        assertThat(result.getMonthlyTrend().get(0).getMonth()).isEqualTo("2026-05");
        assertThat(result.getMonthlyTrend().get(1).getMonth()).isEqualTo("2026-06");
    }
}
