package com.premiumhub.backend.service;

import com.premiumhub.backend.dto.ReceiptDto;
import com.premiumhub.backend.entity.Payment;
import com.premiumhub.backend.entity.Policy;
import com.premiumhub.backend.repository.PaymentRepository;
import com.premiumhub.backend.repository.PolicyRepository;
import com.premiumhub.backend.util.AesUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;
    private final AesUtil aesUtil;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportReceiptPdf(Long paymentId, HttpServletResponse response) throws Exception {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "請款不存在：" + paymentId));

        Policy policy = policyRepository.findById(payment.getPolicyId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "保單不存在"));

        String insuredName = maskName(aesUtil.decrypt(policy.getInsuredName()));

        Map<String, Object> params = new HashMap<>();
        params.put("paymentId",   String.valueOf(payment.getPaymentId()));
        params.put("policyId",    String.valueOf(payment.getPolicyId()));
        params.put("insuredName", insuredName);
        params.put("amount",      payment.getAmount().toPlainString());
        params.put("traceId",     payment.getTraceId());
        params.put("paidAt",      payment.getPaidAt() != null ? payment.getPaidAt().format(FMT) : "-");
        params.put("status",      payment.getStatus());

        InputStream jrxml = new ClassPathResource("reports/receipt.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
            jasperReport, params, new JREmptyDataSource());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"receipt-" + paymentId + ".pdf\"");

        // 流式輸出，防 OOM
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    public List<ReceiptDto> getPaymentList() {
        return paymentRepository.findAllByOrderByPaidAtDesc().stream()
            .map(p -> {
                ReceiptDto dto = new ReceiptDto();
                dto.setPaymentId(String.valueOf(p.getPaymentId()));
                dto.setPolicyId(String.valueOf(p.getPolicyId()));
                dto.setAmount(p.getAmount().toPlainString());
                dto.setTraceId(p.getTraceId());
                dto.setPaidAt(p.getPaidAt() != null ? p.getPaidAt().format(FMT) : "-");
                dto.setStatus(p.getStatus());
                dto.setInsuredName("-");
                return dto;
            }).toList();
    }

    private String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        return name.charAt(0) + "○" + name.substring(2);
    }
}
