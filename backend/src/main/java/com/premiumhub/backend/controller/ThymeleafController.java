package com.premiumhub.backend.controller;

import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.PolicyRepository;
import com.premiumhub.backend.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class ThymeleafController {

    private final PaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;
    private final AesUtil aesUtil;

    @GetMapping("/report-preview/{paymentId}")
    public String receiptPreview(@PathVariable Long paymentId, Model model) throws Exception {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "請款不存在"));

        Policy policy = policyRepository.findById(payment.getPolicyId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "保單不存在"));

        String name = aesUtil.decrypt(policy.getInsuredName());
        String masked = name.length() >= 2 ? name.charAt(0) + "○" + name.substring(2) : name;

        model.addAttribute("payment", payment);
        model.addAttribute("insuredName", masked);
        return "receipt-preview";
    }
}
