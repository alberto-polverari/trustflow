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
        @RequestParam("documentId") Long documentId,
        @RequestParam("comment") String user,
        @RequestParam("approved") boolean approved
    ) {
        workflowService.approve(documentId, user, approved);
        return ResponseEntity.ok("Approvazione completata con successo");
    }

}
