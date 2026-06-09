package com.premiumhub.backend.controller;

import com.premiumhub.backend.dto.PolicyQueryRequest;
import com.premiumhub.backend.dto.PolicyResponse;
import com.premiumhub.backend.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(PolicyQueryRequest req) {
        return ResponseEntity.ok(policyService.queryPolicies(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }
}
