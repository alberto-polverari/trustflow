package it.trustflow.document.service;

import it.trustflow.document.dto.AuditLog;
import it.trustflow.document.entity.*;
import it.trustflow.document.enums.ApprovalStatusEnum;
import it.trustflow.document.enums.DocumentStatusEnum;
import it.trustflow.document.enums.WorkflowStatusEnum;
import it.trustflow.document.enums.WorkflowTypeEnum;
import it.trustflow.document.exception.WorkflowException;
import it.trustflow.document.repository.*;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public WorkflowInstance startWorkflow(Long documentId, HttpServletRequest request) {
        LOGGER.info("Starting workflow for document: {}", documentId);
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        WorkflowDefinition definition = definitionRepo.findByTenantId(user.getTenantId())
                .orElseThrow(() -> new WorkflowException("Configurazione workflow non trovata per il tenant"));

        LOGGER.info("Workflow definition type : {}", definition.getType());
        List<WorkflowStep> steps = stepRepo.findByWorkflowDefinitionOrderByStepOrderAsc(definition);

        Optional<Document> docOpt = documentRepo.findByIdAndTenantId(documentId, user.getTenantId());
        if (docOpt.isEmpty()) {
            LOGGER.error("Document with ID {} not found for tenant {}", documentId, user.getTenantId());
            throw new WorkflowException("Documento non trovato");
        }
        Document document = docOpt.get();
        LOGGER.info("Document '{}' found", document.getFilename());

        // Controllo che il documento non sia già in un workflow
        if (
            document.getStatus().equals(DocumentStatusEnum.IN_REVISIONE.name()) ||
            document.getStatus().equals(DocumentStatusEnum.APPROVATO.name())
        ) {
            LOGGER.error("Documento {} già in revisione o approvato", documentId);
            throw new WorkflowException("Documento già in revisione o approvato");
        }

        WorkflowInstance instance = WorkflowInstance.builder()
                .documentId(document.getId())
                .tenantId(user.getTenantId())
                .workflowDefinition(definition)
                .status(WorkflowStatusEnum.IN_ATTESA.name())
                .startedAt(LocalDateTime.now())
                .build();
        instanceRepo.save(instance);
        LOGGER.info("Instance {} created", instance.getId());

        steps.forEach(step -> {
            Approval approval = Approval.builder()
                    .workflowInstance(instance)
                    .approverId(step.getApproverId())
                    .status(ApprovalStatusEnum.IN_ATTESA.name())
                    .stepOrder(step.getStepOrder())
                    .build();
            approvalRepo.save(approval);
        });
        LOGGER.info("Steps for instance {} created", instance.getId());

        // Aggiorno lo stato del documento associato all'istanza mettendolo in stato IN_REVISIONE

        document.setStatus(DocumentStatusEnum.IN_REVISIONE.name());
        document.setDataModifica(LocalDateTime.now());
        documentRepo.save(document);

        LOGGER.info("Document status updated to {}", document.getStatus());
        AuditLog log = AuditLog.builder()
            .tenantId(user.getTenantId().toString())
            .eventType("WORKFLOW_START")
            .eventDescription("Inizio del processo di approvazione del documento")
            .eventMessage("User " + user.getUsername() + " started document validation: {}" + documentId)
            .userId(user.getUsername())
            .build();
        auditLogService.sendAuditLog(log, request);

        return instance;
    }

    @Transactional
    public void approve(Long documentId, String comment, boolean accepted, HttpServletRequest request) {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();

        LOGGER.info("Retrieving instance by document id {}", documentId);
        Optional<WorkflowInstance> instanceOpt = instanceRepo.findByDocumentIdAndTenantId(documentId, user.getTenantId());

        if (instanceOpt.isEmpty()) {
            LOGGER.error("Workflow instance not found for document {} and tenant {}", documentId, user.getTenantId());
            throw new WorkflowException("Istanza di workflow non trovata per il documento " + documentId);
        }

        WorkflowInstance instance = instanceOpt.get();
        Long instanceId = instance.getId();

        Optional<Document> document = documentRepo.findByIdAndTenantId(instance.getDocumentId(), user.getTenantId());
        if (document.isEmpty()) {
            LOGGER.error("Documento associato all'istanza {} non trovato", instanceId);
            throw new WorkflowException("Documento associato all'istanza non trovato");
        }

        LOGGER.info("Retrieving approval status for {}", user.getUsername());
        Approval approval = approvalRepo.findByWorkflowInstanceIdAndApproverId(instanceId, user.getUsername())
                .orElseThrow(() -> new WorkflowException("Approvazione non trovata"));

        if (!approval.getStatus().equals(ApprovalStatusEnum.IN_ATTESA.name())) {
            LOGGER.error("Approvazione istanza {} già espressa", instanceId);
            throw new WorkflowException("Approvazione già espressa");
        }

        if (instance.getWorkflowDefinition().getType().equals(WorkflowTypeEnum.SEQUENZIALE.name())) {
            LOGGER.info("Workflow type is SEQUENZIALE, checking step order for approver {}", user.getUsername());
            // Controllo che l'approvatore sia il prossimo nella sequenza
            List<Approval> approvals = approvalRepo.findByWorkflowInstanceId(instanceId);

            int nextStepOrder = approvals.stream()
                    .filter(a -> a.getStatus().equals(ApprovalStatusEnum.IN_ATTESA.name()))
                    .mapToInt(Approval::getStepOrder)
                    .findFirst()
                    .orElseThrow(() -> new WorkflowException("Nessun passo in attesa trovato"));

            if (approval.getStepOrder() != nextStepOrder) {
                LOGGER.error("Non è il turno dell'approvatore {} per questa istanza", user.getUsername());
                throw new WorkflowException("Non è il turno dell'approvatore per questa istanza");
            }
        }

        approval.setStatus(accepted ? ApprovalStatusEnum.APPROVATO.name() : ApprovalStatusEnum.RIFIUTATO.name());
        approval.setComment(comment);
        approval.setApprovedAt(LocalDateTime.now());
        LOGGER.info("Approving instance {} for user {} with status {}", instanceId, user.getUsername(), approval.getStatus());
        approvalRepo.save(approval);

        List<Approval> allApprovals = approvalRepo.findByWorkflowInstanceId(instanceId);
        LOGGER.info("Checking all approvals for instance {}", instanceId);
        boolean allApproved = allApprovals.stream().allMatch(a -> a.getStatus().equals(ApprovalStatusEnum.APPROVATO.name()));
        // controllo che siano state date tutte le approvazioni (nessuna in attesa) e che ci sia almeno un rifiuto
        boolean anyRejected = allApprovals.stream().noneMatch(a -> a.getStatus().equals(ApprovalStatusEnum.IN_ATTESA.name()))
            && allApprovals.stream().anyMatch(a -> a.getStatus().equals(ApprovalStatusEnum.RIFIUTATO.name()));

        // l'istanza di workflow e il documento passano in stato REVISIONE_UTENTE se c'è almeno un rifiuto
        if (anyRejected) {
            LOGGER.info("Workflow instance {} has at least one rejection, setting status to REVISIONE_UTENTE", instanceId);
            instance.setStatus(WorkflowStatusEnum.RIFIUTATO.name());
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // aggiorno lo stato del documento associato all'istanza
            Document doc = document.get();
            doc.setStatus(DocumentStatusEnum.REVISIONE_UTENTE.name());
            doc.setDataModifica(LocalDateTime.now());
            LOGGER.info("Updating document status to REVISIONE_UTENTE for instance {}", instanceId);
            documentRepo.save(doc);

            return;
        }

        if (allApproved) {
            LOGGER.info("All approvals for instance {} have been granted", instanceId);
            instance.setStatus(WorkflowStatusEnum.APPROVATO.name());
            instance.setCompletedAt(LocalDateTime.now());
            instanceRepo.save(instance);

            // recupero il documento associato all'istanza e aggiorno lo stato
            Document doc = document.get();
            doc.setStatus(DocumentStatusEnum.APPROVATO.name());
            doc.setDataModifica(LocalDateTime.now());
            LOGGER.info("Updating document status to APPROVATO for instance {}", instanceId);
            documentRepo.save(doc);

            // firma digitale
            LOGGER.info("Invoco servizio di firma digitale per {}", doc.getFilename());
            String signedDoc = integrationService.firmaDocumento(doc.getFilename());
            AuditLog log = AuditLog.builder()
                .tenantId(user.getTenantId().toString())
                .eventType("FIRMA_DOCUMENTO")
                .eventDescription("Chiamata al servizio di firma digitale")
                .eventMessage("User " + user.getUsername() + " signed document: {}" + doc.getId())
                .userId(user.getUsername())
                .build();
            auditLogService.sendAuditLog(log, request);

            // conservazione su sistema esterno
            LOGGER.info("Invoco servizio di invio documento per {}", doc.getFilename());
            integrationService.invioDocumento(signedDoc);
            log = AuditLog.builder()
                .tenantId(user.getTenantId().toString())
                .eventType("CONSERVAZIONE_DOCUMENTO")
                .eventDescription("Chiamata al servizio di conservazione del documento")
                .eventMessage("User " + user.getUsername() + " saved signed document: {}" + doc.getId())
                .userId(user.getUsername())
                .build();
            auditLogService.sendAuditLog(log, request);
        }
    }
}
