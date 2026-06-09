package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.PolicyQueryRequest;
import com.premiumhub.backend.dto.PolicyResponse;
import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.mapper.PolicyMapper;
import com.premiumhub.backend.repository.PolicyRepository;
import com.premiumhub.backend.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyMapper policyMapper;
    private final PolicyRepository policyRepository;
    private final AesUtil aesUtil;

    // 白名單，防止 ${sortColumn} SQL Injection
    private static final List<String> ALLOWED_SORT_COLUMNS =
            List.of("policy_id", "policy_no", "premium_amount", "due_date", "status");

    public Map<String, Object> queryPolicies(PolicyQueryRequest req) {
        String sortColumn = ALLOWED_SORT_COLUMNS.contains(req.getSortColumn())
                ? req.getSortColumn() : "policy_id";
        String sortDir = "DESC".equalsIgnoreCase(req.getSortDir()) ? "DESC" : "ASC";
        int offset = req.getPage() * req.getSize();

        List<Policy> policies = policyMapper.findByCondition(
                req.getPolicyNo(), req.getStatus(),
                req.getDueDateFrom(), req.getDueDateTo(),
                sortColumn, sortDir, req.getSize(), offset);

        long total = policyMapper.countByCondition(
                req.getPolicyNo(), req.getStatus(),
                req.getDueDateFrom(), req.getDueDateTo());

        List<PolicyResponse> content = policies.stream()
                .map(this::toResponse)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("total", total);
        result.put("page", req.getPage());
        result.put("size", req.getSize());
        return result;
    }

    public PolicyResponse getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found: " + id));
        return toResponse(policy);
    }

    private PolicyResponse toResponse(Policy p) {
        PolicyResponse r = new PolicyResponse();
        r.setPolicyId(p.getPolicyId());
        r.setPolicyNo(p.getPolicyNo());
        r.setPremiumAmount(p.getPremiumAmount());
        r.setStatus(p.getStatus());
        r.setDueDate(p.getDueDate());
        r.setVersion(p.getVersion());

        // 解密姓名後遮罩（王○明）
        try {
            String name = aesUtil.decrypt(p.getInsuredName());
            r.setInsuredName(maskName(name));
        } catch (Exception e) {
            r.setInsuredName("***");
        }

        // 解密身分證後遮罩（A12****890）
        try {
            String idNo = aesUtil.decrypt(p.getIdNoEncrypted());
            r.setIdNoMasked(maskIdNo(idNo));
        } catch (Exception e) {
            r.setIdNoMasked("***");
        }

        return r;
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) return "*";
        if (name.length() == 2) return name.charAt(0) + "*";
        return name.charAt(0) + "○" + name.charAt(name.length() - 1);
    }

    private String maskIdNo(String idNo) {
        if (idNo == null || idNo.length() < 7) return "***";
        return idNo.substring(0, 3) + "****" + idNo.substring(idNo.length() - 3);
    }
}
