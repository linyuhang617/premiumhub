package com.premiumhub.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long policyId;
    private String policyNo;
    private BigDecimal amount;
    private String status;
    private String traceId;
    private LocalDateTime paidAt;
}
