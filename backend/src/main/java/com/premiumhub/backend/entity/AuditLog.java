package com.premiumhub.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "AUDIT_LOG")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "trace_id", nullable = false)
    private String traceId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "operator", nullable = false)
    private String operator;

    @Column(name = "result", nullable = false)
    private String result; // SUCCESS / FAILED

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
