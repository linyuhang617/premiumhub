package com.premiumhub.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "保單 ID 不能為空")
    private Long policyId;

    @NotNull(message = "金額不能為空")
    @DecimalMin(value = "0.01", message = "金額必須大於 0")
    private BigDecimal amount;
}
