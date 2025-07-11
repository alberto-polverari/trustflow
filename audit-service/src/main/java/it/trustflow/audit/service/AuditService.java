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
        String ip = getClientIp(request);
        AuditLog log = new AuditLog();
        log.setIp(ip);
        log.setUserId(audit.getUserId());
        log.setTenantId(audit.getTenantId());
        log.setEventType(audit.getEventType());
        log.setEventDescription(audit.getEventDescription());
        log.setEventMessage(audit.getEventMessage());
        auditLogRepository.save(log);
        return "Audit log saved successfully";
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // se ci sono più IP, prendi il primo (l’originale)
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr(); // fallback
    }
}
