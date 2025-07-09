package it.trustflow.document.service;

import it.trustflow.document.entity.*;
import it.trustflow.document.repository.*;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkflowService {

    @Autowired
    private UserUtils userUtils;
    @Autowired
    private WorkflowDefinitionRepository definitionRepo;
    @Autowired
    private WorkflowStepRepository stepRepo;
    @Autowired
    private WorkflowInstanceRepository instanceRepo;
    @Autowired
    private ApprovalRepository approvalRepo;
    @Autowired
    private DocumentRepository documentRepo;

    @Transactional
    public WorkflowInstance startWorkflow(Long documentId) {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        WorkflowDefinition definition = definitionRepo.findByTenantId(user.getTenantId())
                .orElseThrow(() -> new RuntimeException("Configurazione workflow non trovata per il tenant"));

        List<WorkflowStep> steps = stepRepo.findByWorkflowDefinitionOrderByStepOrderAsc(definition);
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento non trovato"));
        WorkflowInstance instance = WorkflowInstance.builder()
                .documentId(document.getId())
                .tenantId(user.getTenantId())
                .workflowDefinition(definition)
                .status("IN_CORSO")
                .startedAt(LocalDateTime.now())
                .build();
        instanceRepo.save(instance);

        steps.forEach(step -> {
            Approval approval = Approval.builder()
                    .workflowInstance(instance)
                    .approverId(step.getApproverId())
                    .status("IN_ATTESA")
                    .stepOrder(step.getStepOrder())
                    .build();
            approvalRepo.save(approval);
        });

        // Aggiorno lo stato del documento associato all'istanza mettendolo in stato IN_REVISIONE

        document.setStatus("IN_REVISIONE");
        document.setDataModifica(LocalDateTime.now());
        documentRepo.save(document);

        return instance;
    }

    @Transactional
    public void approve(Long instanceId, String comment, boolean accepted) {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();

        WorkflowInstance instance = instanceRepo.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Istanza non trovata"));

        Approval approval = approvalRepo.findByWorkflowInstanceIdAndApproverId(instanceId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("Approvazione non trovata"));

        if (!approval.getStatus().equals("IN_ATTESA")) {
            throw new RuntimeException("Approvazione già espressa");
        }

        if (instance.getWorkflowDefinition().getType().equals("SEQUENZIALE")) {
            // Controllo che l'approvatore sia il prossimo nella sequenza
            List<Approval> approvals = approvalRepo.findByWorkflowInstanceId(instanceId);

            int nextStepOrder = approvals.stream()
                    .filter(a -> a.getStatus().equals("IN_ATTESA"))
                    .mapToInt(Approval::getStepOrder)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nessun passo in attesa trovato"));

            if (approval.getStepOrder() != nextStepOrder) {
                throw new RuntimeException("Non è il turno dell'approvatore per questa istanza");
            }
        }

        approval.setStatus(accepted ? "APPROVATO" : "RIFIUTATO");
        approval.setComment(comment);
        approval.setApprovedAt(LocalDateTime.now());
        approvalRepo.save(approval);

        if (!accepted) {
            instance.setStatus("RIFIUTATO");
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);
            return;
        }

        List<Approval> allApprovals = approvalRepo.findByWorkflowInstanceId(instanceId);
        boolean allApproved = allApprovals.stream().allMatch(a -> a.getStatus().equals("APPROVATO"));
        // controllo che siano state date tutte le approvazioni (nessuna in attesa) e che ci sia almeno un rifiuto

        boolean anyRejected = allApprovals.stream().noneMatch(a -> a.getStatus().equals("IN_ATTESA"))
            && allApprovals.stream().anyMatch(a -> a.getStatus().equals("RIFIUTATO"));

        // l'istanza di workflow e il documento passano in stato REVISIONE_UTENTE se c'è almeno un rifiuto
        if (anyRejected) {
            instance.setStatus("REVISIONE_UTENTE");
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // recupero il documento associato all'istanza e aggiorno lo stato
            Optional<Document> document = documentRepo.findById(instance.getDocumentId());
            if (document.isPresent()) {
                Document doc = document.get();
                doc.setStatus("REVISIONE_UTENTE");
                doc.setDataModifica(LocalDateTime.now());
                documentRepo.save(doc);
            } else {
                throw new RuntimeException("Documento associato all'istanza non trovato");
            }

            return;
        }

        if (allApproved) {
            instance.setStatus("APPROVATO");
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // recupero il documento associato all'istanza e aggiorno lo stato
            Optional<Document> document = documentRepo.findById(instance.getDocumentId());
            if (document.isPresent()) {
                Document doc = document.get();
                doc.setStatus("APPROVATO");
                doc.setDataModifica(LocalDateTime.now());
                documentRepo.save(doc);
            } else {
                throw new RuntimeException("Documento associato all'istanza non trovato");
            }

            // firma digitale
            // conservazione su sistema esterno
        }
    }
}
