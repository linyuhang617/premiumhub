package com.premiumhub.backend.service;

import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.SealAuth;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.SealAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SealAuthService {

    private final PaymentRepository paymentRepository;
    private final SealAuthRepository sealAuthRepository;

    @Transactional(rollbackFor = Exception.class)
    public SealAuth authorize(Long paymentId, String operator, String remark) {

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "請款不存在：" + paymentId));

        if (!"PENDING".equals(payment.getStatus())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "只有 PENDING 請款可核印，當前狀態：" + payment.getStatus());
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        SealAuth auth = new SealAuth();
        auth.setPayment(payment);
        auth.setAuthorizedBy(operator);
        auth.setAuthorizedAt(LocalDateTime.now());
        auth.setRemark(remark);
        return sealAuthRepository.save(auth);
    }

    public List<SealAuth> findAll() {
        return sealAuthRepository.findAllWithPayment();
    }

    public List<Payment> findPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }
}
