package it.trustflow.document.controller;

import it.trustflow.document.entity.WorkflowInstance;
import it.trustflow.document.service.WorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/startRevisione")
    @PreAuthorize("hasAuthority('ROLE_APPROVER')")
    public ResponseEntity<WorkflowInstance> startRevisione(
        @RequestParam("documentId") Long documentId,
        HttpServletRequest request
    ) {
        return ResponseEntity.ok(workflowService.startWorkflow(documentId, request));
    }

    @PutMapping("/approva")
    @PreAuthorize("hasAuthority('ROLE_APPROVER')")
    public ResponseEntity<String> approva(
        @RequestParam("documentId") Long documentId,
        @RequestParam("comment") String comment,
        @RequestParam("approved") boolean approved,
        HttpServletRequest request
    ) {
        workflowService.approve(documentId, comment, approved, request);
        return ResponseEntity.ok("Valutazione inviata con successo");
    }

}
