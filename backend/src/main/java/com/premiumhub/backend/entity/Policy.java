package com.premiumhub.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "POLICY")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    @Column(name = "policy_no", nullable = false, unique = true)
    private String policyNo;

    // 存密文
    @Column(name = "insured_name", nullable = false)
    private String insuredName;

    // 存密文
    @Column(name = "id_no_encrypted")
    private String idNoEncrypted;

    @Column(name = "premium_amount", nullable = false)
    private BigDecimal premiumAmount;

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE / EXPIRED / CANCELLED

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
