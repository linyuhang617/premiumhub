package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.PaymentRequest;
import com.premiumhub.backend.dto.PaymentResponse;
import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogService auditLogService;

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse executePayment(PaymentRequest request) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("[{}] 開始請款 policyId={} amount={}", traceId, request.getPolicyId(), request.getAmount());

        try {
            // 悲觀鎖鎖定保單，防止並發重複扣款
            Policy policy = policyRepository.findByIdWithLock(request.getPolicyId())
                    .orElseThrow(() -> new RuntimeException("保單不存在：" + request.getPolicyId()));

            if (!"ACTIVE".equals(policy.getStatus())) {
                throw new RuntimeException("保單狀態非有效，無法請款：" + policy.getStatus());
            }

            // 建立請款紀錄
            Payment payment = new Payment();
            payment.setPolicyId(policy.getPolicyId());
            payment.setAmount(request.getAmount());
            payment.setStatus("SUCCESS");
            payment.setTraceId(traceId);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("[{}] 請款成功 paymentId={}", traceId, payment.getPaymentId());

            // REQUIRES_NEW：獨立事務寫入，主流程 rollback 也不影響
            auditLogService.log(traceId, "PAYMENT", operator, "SUCCESS", null);

            return toResponse(payment, policy.getPolicyNo());

        } catch (Exception e) {
            log.error("[{}] 請款失敗：{}", traceId, e.getMessage());
            // 主流程即將 rollback，但 REQUIRES_NEW 確保 log 仍寫入
            auditLogService.log(traceId, "PAYMENT", operator, "FAILED", e.getMessage());
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }

    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAllByOrderByPaidAtDesc().stream()
                .map(p -> toResponse(p, null))
                .collect(Collectors.toList());
    }

    private PaymentResponse toResponse(Payment p, String policyNo) {
        return new PaymentResponse(
                p.getPaymentId(),
                p.getPolicyId(),
                policyNo,
                p.getAmount(),
                p.getStatus(),
                p.getTraceId(),
                p.getPaidAt()
        );
    }
}
