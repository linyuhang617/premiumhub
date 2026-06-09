package com.premiumhub.backend.service;

import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.SealAuth;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.SealAuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SealAuthServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SealAuthRepository sealAuthRepository;

    @InjectMocks
    private SealAuthService sealAuthService;

    @Test
    void authorize_success() {
        Payment payment = new Payment();
        payment.setPaymentId(1L);
        payment.setStatus("PENDING");
        payment.setAmount(new BigDecimal("12000"));

        SealAuth saved = new SealAuth();
        saved.setAuthId(1L);
        saved.setPayment(payment);
        saved.setAuthorizedBy("admin");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(sealAuthRepository.save(any())).thenReturn(saved);

        SealAuth result = sealAuthService.authorize(1L, "admin", "核印通過");

        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("admin", result.getAuthorizedBy());
        verify(paymentRepository).save(payment);
        verify(sealAuthRepository).save(any());
    }

    @Test
    void authorize_notPending_throws400() {
        Payment payment = new Payment();
        payment.setPaymentId(2L);
        payment.setStatus("SUCCESS");

        when(paymentRepository.findById(2L)).thenReturn(Optional.of(payment));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> sealAuthService.authorize(2L, "admin", ""));

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("只有 PENDING"));
    }

    @Test
    void authorize_paymentNotFound_throws404() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> sealAuthService.authorize(999L, "admin", ""));

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("請款不存在"));
    }
}
