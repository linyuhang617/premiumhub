package com.premiumhub.backend.controller;

import com.premiumhub.backend.dto.ReceiptDto;
import com.premiumhub.backend.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 下載單筆收據 PDF（流式輸出）
    @GetMapping("/receipt/{paymentId}")
    public void downloadReceipt(
            @PathVariable Long paymentId,
            HttpServletResponse response) throws Exception {
        reportService.exportReceiptPdf(paymentId, response);
    }

    // 取得請款列表（供前端匯出用）
    @GetMapping("/payment-list")
    public List<ReceiptDto> paymentList() {
        return reportService.getPaymentList();
    }
}
