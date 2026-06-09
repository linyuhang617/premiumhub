package com.premiumhub.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolicyRequest {

    @NotBlank(message = "保單號不可為空")
    private String policyNo;

    @NotBlank(message = "被保人姓名不可為空")
    private String insuredName;

    @NotBlank(message = "身分證號不可為空")
    private String idNo;

    @NotNull(message = "保費金額不可為空")
    @DecimalMin(value = "0.01", message = "保費金額必須大於 0")
    private BigDecimal premiumAmount;

    @NotBlank(message = "狀態不可為空")
    private String status;

    private LocalDate dueDate;

    // 編輯時必填，新增時為 null
    private Long version;
}
