package it.trustflow.document.controller;

import it.trustflow.document.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @PostMapping("/startRevisione")
    public ResponseEntity<String> startRevisione(
            @RequestParam("documentId") Long documentId
    ) {
        workflowService.startWorkflow(documentId);
        return ResponseEntity.ok("Workflow di revisione avviato con successo");
    }

    @PutMapping("/approva")
    public ResponseEntity<String> approva(
        @RequestParam("instanceId") Long instanceId,
        @RequestParam("comment") String comment,
        @RequestParam("approved") boolean approved
    ) {
        workflowService.approve(instanceId, comment, approved);
        return ResponseEntity.ok("Approvazione completata con successo");
    }

}
