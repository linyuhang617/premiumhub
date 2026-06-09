package com.premiumhub.backend.repository;

import com.premiumhub.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderByPaidAtDesc();
    List<Payment> findByStatus(String status);
}
