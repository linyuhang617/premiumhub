package com.premiumhub.backend.dto;

import lombok.Data;

@Data
public class ReceiptDto {
    private String paymentId;
    private String policyId;
    private String insuredName;
    private String amount;
    private String traceId;
    private String paidAt;
    private String status;
}
