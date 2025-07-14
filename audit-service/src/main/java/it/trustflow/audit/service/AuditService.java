package it.trustflow.audit.service;

import it.trustflow.audit.dto.Audit;
import it.trustflow.audit.entity.AuditLog;
import it.trustflow.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public String log(Audit audit, HttpServletRequest request) {
        AuditLog log = new AuditLog();
        log.setIp(audit.getIp());
        log.setUserId(audit.getUserId());
        log.setTenantId(audit.getTenantId());
        log.setEventType(audit.getEventType());
        log.setEventDescription(audit.getEventDescription());
        log.setEventMessage(audit.getEventMessage());
        auditLogRepository.save(log);
        return "Audit log saved successfully";
    }
}
