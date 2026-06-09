package com.premiumhub.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolicyResponse {
    private Long policyId;
    private String policyNo;
    private String insuredName;   // 解密後遮罩
    private String idNoMasked;    // 遮罩後身分證
    private BigDecimal premiumAmount;
    private String status;
    private LocalDate dueDate;
    private Long version;
}
