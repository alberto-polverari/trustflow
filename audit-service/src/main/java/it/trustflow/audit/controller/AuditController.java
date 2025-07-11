package it.trustflow.audit.controller;

import it.trustflow.audit.dto.Audit;
import it.trustflow.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/log")
    public String logAuditEvent(HttpServletRequest request, @RequestBody Audit auditLog) {
        return auditService.log(auditLog, request);
    }
}
