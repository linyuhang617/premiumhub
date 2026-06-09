package com.premiumhub.backend.controller;

import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.SealAuth;
import com.premiumhub.backend.service.SealAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seal-auth")
@RequiredArgsConstructor
public class SealAuthController {

    private final SealAuthService sealAuthService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{paymentId}")
    public ResponseEntity<SealAuth> authorize(
            @PathVariable Long paymentId,
            @RequestParam(defaultValue = "") String remark,
            Authentication authentication) {

        String operator = authentication.getName();
        SealAuth auth = sealAuthService.authorize(paymentId, operator, remark);
        return ResponseEntity.ok(auth);
    }

    @GetMapping
    public ResponseEntity<List<SealAuth>> list() {
        return ResponseEntity.ok(sealAuthService.findAll());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> pendingList() {
        return ResponseEntity.ok(sealAuthService.findPendingPayments());
    }
}
