package com.premiumhub.backend.service;

import com.premiumhub.backend.entity.AuditLog;
import com.premiumhub.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String traceId, String action, String operator, String result, String remark) {
        AuditLog log = new AuditLog();
        log.setTraceId(traceId);
        log.setAction(action);
        log.setOperator(operator);
        log.setResult(result);
        log.setRemark(remark);
        auditLogRepository.save(log);
    }
}
