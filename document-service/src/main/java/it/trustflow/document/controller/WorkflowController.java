package it.trustflow.document.controller;

import it.trustflow.document.dto.Approval;
import it.trustflow.document.entity.WorkflowInstance;
import it.trustflow.document.service.WorkflowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/{instanceId}")
    @PreAuthorize("hasAuthority('ROLE_APPROVER')")
    public ResponseEntity<WorkflowInstance> getWorkflowInstance(
        @PathVariable("instanceId") Long instanceId
    ) {
        return ResponseEntity.ok(workflowService.getWorkflowInstance(instanceId));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_APPROVER')")
    public ResponseEntity<List<WorkflowInstance>> getWorkflowInstance() {
        return ResponseEntity.ok(workflowService.getAllWorkflowInstances());
    }

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
        @RequestBody Approval approval,
        HttpServletRequest request
    ) {
        workflowService.approve(approval.getDocumentId(), approval.getComment(), approval.isApproved(), request);
        return ResponseEntity.ok("Valutazione inviata con successo");
    }

}
