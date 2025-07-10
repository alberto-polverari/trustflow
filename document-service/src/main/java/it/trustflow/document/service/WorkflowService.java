package it.trustflow.document.service;

import it.trustflow.document.entity.*;
import it.trustflow.document.repository.*;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkflowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowService.class);

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
        LOGGER.info("Starting workflow for document: {}", documentId);
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        WorkflowDefinition definition = definitionRepo.findByTenantId(user.getTenantId())
                .orElseThrow(() -> new RuntimeException("Configurazione workflow non trovata per il tenant"));

        LOGGER.info("Workflow definition type : {}", definition.getType());
        List<WorkflowStep> steps = stepRepo.findByWorkflowDefinitionOrderByStepOrderAsc(definition);
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento non trovato"));
        LOGGER.info("Document '{}' found", document.getFilename());
        WorkflowInstance instance = WorkflowInstance.builder()
                .documentId(document.getId())
                .tenantId(user.getTenantId())
                .workflowDefinition(definition)
                .status("IN_CORSO")
                .startedAt(LocalDateTime.now())
                .build();
        instanceRepo.save(instance);
        LOGGER.info("Instance {} created", instance.getId());

        steps.forEach(step -> {
            Approval approval = Approval.builder()
                    .workflowInstance(instance)
                    .approverId(step.getApproverId())
                    .status("IN_ATTESA")
                    .stepOrder(step.getStepOrder())
                    .build();
            approvalRepo.save(approval);
        });
        LOGGER.info("Steps for instance {} created", instance.getId());

        // Aggiorno lo stato del documento associato all'istanza mettendolo in stato IN_REVISIONE

        document.setStatus("IN_REVISIONE");
        document.setDataModifica(LocalDateTime.now());
        documentRepo.save(document);

        LOGGER.info("Document status updated to {}", document.getStatus());
        return instance;
    }

    @Transactional
    public void approve(Long instanceId, String comment, boolean accepted) {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();

        LOGGER.info("Retrieving instance {}", instanceId);
        WorkflowInstance instance = instanceRepo.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("Istanza non trovata"));

        Optional<Document> document = documentRepo.findById(instance.getDocumentId());
        if (!document.isPresent()) {
            LOGGER.error("Documento associato all'istanza {} non trovato", instanceId);
            throw new RuntimeException("Documento associato all'istanza non trovato");
        }

        LOGGER.info("Retrieving approval status for {}", user.getUsername());
        Approval approval = approvalRepo.findByWorkflowInstanceIdAndApproverId(instanceId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("Approvazione non trovata"));

        if (!approval.getStatus().equals("IN_ATTESA")) {
            LOGGER.error("Approvazione istanza {} già espressa", instanceId);
            throw new RuntimeException("Approvazione già espressa");
        }

        if (instance.getWorkflowDefinition().getType().equals("SEQUENZIALE")) {
            LOGGER.info("Workflow type is SEQUENZIALE, checking step order for approver {}", user.getUsername());
            // Controllo che l'approvatore sia il prossimo nella sequenza
            List<Approval> approvals = approvalRepo.findByWorkflowInstanceId(instanceId);

            int nextStepOrder = approvals.stream()
                    .filter(a -> a.getStatus().equals("IN_ATTESA"))
                    .mapToInt(Approval::getStepOrder)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nessun passo in attesa trovato"));

            if (approval.getStepOrder() != nextStepOrder) {
                LOGGER.error("Non è il turno dell'approvatore {} per questa istanza", user.getUsername());
                throw new RuntimeException("Non è il turno dell'approvatore per questa istanza");
            }
        }

        approval.setStatus(accepted ? "APPROVATO" : "RIFIUTATO");
        approval.setComment(comment);
        approval.setApprovedAt(LocalDateTime.now());
        LOGGER.info("Approving instance {} for user {} with status {}", instanceId, user.getUsername(), approval.getStatus());
        approvalRepo.save(approval);
//
//        if (!accepted) {
//            LOGGER.info("Approval for instance {} rejected by {}", instanceId, user.getUsername());
//            instance.setStatus("RIFIUTATO");
//            instance.setCompletedAt(LocalDateTime.now());
//            instanceRepo.save(instance);
//            return;
//        }

        List<Approval> allApprovals = approvalRepo.findByWorkflowInstanceId(instanceId);
        LOGGER.info("Checking all approvals for instance {}", instanceId);
        boolean allApproved = allApprovals.stream().allMatch(a -> a.getStatus().equals("APPROVATO"));
        // controllo che siano state date tutte le approvazioni (nessuna in attesa) e che ci sia almeno un rifiuto
        boolean anyRejected = allApprovals.stream().noneMatch(a -> a.getStatus().equals("IN_ATTESA"))
            && allApprovals.stream().anyMatch(a -> a.getStatus().equals("RIFIUTATO"));

        // l'istanza di workflow e il documento passano in stato REVISIONE_UTENTE se c'è almeno un rifiuto
        if (anyRejected) {
            LOGGER.info("Workflow instance {} has at least one rejection, setting status to REVISIONE_UTENTE", instanceId);
            instance.setStatus("RIFIUTATO");
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // aggiorno lo stato del documento associato all'istanza
            Document doc = document.get();
            doc.setStatus("REVISIONE_UTENTE");
            doc.setDataModifica(LocalDateTime.now());
            LOGGER.info("Updating document status to REVISIONE_UTENTE for instance {}", instanceId);
            documentRepo.save(doc);

            return;
        }

        if (allApproved) {
            LOGGER.info("All approvals for instance {} have been granted", instanceId);
            instance.setStatus("APPROVATO");
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // recupero il documento associato all'istanza e aggiorno lo stato
            Document doc = document.get();
            doc.setStatus("APPROVATO");
            doc.setDataModifica(LocalDateTime.now());
            LOGGER.info("Updating document status to APPROVATO for instance {}", instanceId);
            documentRepo.save(doc);

            // firma digitale
            // conservazione su sistema esterno
        }
    }
}
