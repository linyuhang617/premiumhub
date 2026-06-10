package com.premiumhub.backend.repository;

import com.premiumhub.backend.dto.DashboardProjection;
import com.premiumhub.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderByPaidAtDesc();
    List<Payment> findByStatus(String status);

    @Query("""
        SELECT COUNT(p) AS totalPayments,
               COALESCE(SUM(p.amount), 0) AS totalAmount,
               SUM(CASE WHEN p.status = 'SUCCESS' THEN 1 ELSE 0 END) AS successCount,
               SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END) AS pendingCount
        FROM Payment p
    """)
    DashboardProjection findDashboardStats();

    @Query("""
        SELECT FORMATDATETIME(p.paidAt, 'yyyy-MM') AS month,
               COUNT(p) AS cnt,
               COALESCE(SUM(p.amount), 0) AS total
        FROM Payment p
        WHERE p.paidAt IS NOT NULL
        GROUP BY FORMATDATETIME(p.paidAt, 'yyyy-MM')
        ORDER BY FORMATDATETIME(p.paidAt, 'yyyy-MM')
    """)
    List<Object[]> findMonthlyTrendRaw();
}
