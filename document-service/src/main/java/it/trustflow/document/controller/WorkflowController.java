package it.trustflow.document.controller;

import it.trustflow.document.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/startRevisione")
    public ResponseEntity<String> startRevisione(
            @RequestParam("documentId") Long documentId,
            @RequestParam("tenantId") Long tenantId
    ) {
        workflowService.startWorkflow(documentId, tenantId);
        return ResponseEntity.ok("Workflow di revisione avviato con successo");
    }



}
