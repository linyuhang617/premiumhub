package com.premiumhub.backend.dto;

import lombok.Data;

@Data
public class PolicyQueryRequest {
    private String policyNo;
    private String status;
    private String dueDateFrom;
    private String dueDateTo;
    private int page = 0;
    private int size = 10;
    private String sortColumn = "policy_id";
    private String sortDir = "ASC";
}
