package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.PaymentRequest;
import com.premiumhub.backend.dto.PaymentResponse;
import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setupSecurityContext() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testUser");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void executePayment_success() {
        Policy policy = new Policy();
        policy.setPolicyId(1L);
        policy.setPolicyNo("POL-001");
        policy.setStatus("ACTIVE");

        when(policyRepository.findByIdWithLock(1L)).thenReturn(Optional.of(policy));

        Payment savedPayment = new Payment();
        savedPayment.setPaymentId(100L);
        savedPayment.setPolicyId(1L);
        savedPayment.setAmount(new BigDecimal("500"));
        savedPayment.setStatus("SUCCESS");
        savedPayment.setTraceId("mock-trace");
        when(paymentRepository.save(any())).thenReturn(savedPayment);

        PaymentRequest request = new PaymentRequest();
        request.setPolicyId(1L);
        request.setAmount(new BigDecimal("500"));

        PaymentResponse response = paymentService.executePayment(request);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getPolicyNo()).isEqualTo("POL-001");
        verify(auditLogService).log(any(), eq("PAYMENT"), eq("testUser"), eq("SUCCESS"), isNull());
    }

    @Test
    void executePayment_policyNotFound() {
        when(policyRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        PaymentRequest request = new PaymentRequest();
        request.setPolicyId(99L);
        request.setAmount(new BigDecimal("100"));

        assertThatThrownBy(() -> paymentService.executePayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("保單不存在");

        verify(auditLogService).log(any(), eq("PAYMENT"), eq("testUser"), eq("FAILED"), any());
    }

    @Test
    void executePayment_policyNotActive() {
        Policy policy = new Policy();
        policy.setPolicyId(2L);
        policy.setPolicyNo("POL-002");
        policy.setStatus("EXPIRED");

        when(policyRepository.findByIdWithLock(2L)).thenReturn(Optional.of(policy));

        PaymentRequest request = new PaymentRequest();
        request.setPolicyId(2L);
        request.setAmount(new BigDecimal("100"));

        assertThatThrownBy(() -> paymentService.executePayment(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("保單狀態非有效");

        verify(auditLogService).log(any(), eq("PAYMENT"), eq("testUser"), eq("FAILED"), any());
    }
}
