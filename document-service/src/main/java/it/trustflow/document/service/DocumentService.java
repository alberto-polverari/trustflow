package it.trustflow.document.service;

import it.trustflow.document.dto.AuditLog;
import it.trustflow.document.entity.Document;
import it.trustflow.document.repository.DocumentRepository;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private DocumentRepository repository;

    /**
     * Uploads a document and logs the action.
     *
     * @param file the document file to upload
     * @param request the HTTP request for logging
     * @return the saved Document entity
     */
    public Document upload(MultipartFile file, HttpServletRequest request) {
        LOGGER.info("Uploading document: {}", file.getOriginalFilename());
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "_" + originalFilename;
        Document doc = Document.builder()
            .filename(newFilename)
            .contentType(file.getContentType())
            .ownerId(user.getUsername())
            .tenantId(user.getTenantId())
            .dataInserimento(LocalDateTime.now())
            .dataModifica(LocalDateTime.now())
            .status("IN_ATTESA")
            .path("")
        .build();
        AuditLog log = AuditLog.builder()
            .tenantId(user.getTenantId().toString())
            .eventType("UPLOAD_DOCUMENT")
            .eventDescription("Upload del documento")
            .eventMessage("User " + user.getUsername() + " uploaded document: {}" + newFilename)
            .userId(user.getUsername().toString())
            .build();
        auditLogService.sendAuditLog(log, request);
        return repository.save(doc);
    }

    /**
     * Uploads a mock document for testing purposes.
     *
     * @param fileName the name of the mock document
     * @param request the HTTP request for logging
     * @return the saved Document entity
     */
    public Document uploadMock(String fileName, HttpServletRequest request)  {
        LOGGER.info("Uploading mock document: {}", fileName);
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        Document doc = Document.builder()
            .filename(fileName)
            .contentType("application/pdf") // Mock content type
            .ownerId(user.getUsername())
            .tenantId(user.getTenantId())
            .dataInserimento(LocalDateTime.now())
            .dataModifica(LocalDateTime.now())
            .status("IN_ATTESA")
            .path("")
        .build();
        LOGGER.info("Upload completed");
        AuditLog log = AuditLog.builder()
            .tenantId(user.getTenantId().toString())
            .eventType("UPLOAD_DOCUMENT_MOCK")
            .eventDescription("Upload del documento mock")
            .eventMessage("User " + user.getUsername() + " uploaded mock document: {}" + fileName)
            .userId(user.getUsername().toString())
            .build();
        auditLogService.sendAuditLog(log, request);
        return repository.save(doc);
    }

    /**
     * Finds a document by its ID, owner ID, and tenant ID.
     *
     * @param id the ID of the document
     * @return an Optional containing the Document if found, or empty if not found
     */
    public Optional<Document> findById(Long id) {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        return repository.findByIdAndOwnerIdAndTenantId(id, user.getUsername(), user.getTenantId());
    }

    /**
     * Finds a document by its ID and tenant ID.
     *
     * @param id the ID of the document
     * @return an Optional containing the Document if found, or empty if not found
     */
    public List<Document> findAllUserDocuments() {
        AuthenticatedUser user = userUtils.getAuthenticatedUser();
        return repository.findByOwnerIdAndTenantId(user.getUsername(), user.getTenantId());
    }

}
