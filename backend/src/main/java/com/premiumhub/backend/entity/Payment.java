package com.premiumhub.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PAYMENT")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status; // SUCCESS / FAILED

    @Column(name = "trace_id", nullable = false, unique = true)
    private String traceId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
