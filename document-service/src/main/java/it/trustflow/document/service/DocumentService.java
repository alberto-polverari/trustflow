package it.trustflow.document.service;

import it.trustflow.document.entity.Document;
import it.trustflow.document.repository.DocumentRepository;
import it.trustflow.document.security.dto.AuthenticatedUser;
import it.trustflow.document.util.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private DocumentRepository repository;

    public Document upload(MultipartFile file) {
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
        return repository.save(doc);
    }

    public Document uploadMock(String fileName)  {
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
        return repository.save(doc);
    }

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public Document updateStatus(Long id, String status) {
        Document doc = repository.findById(id).orElseThrow();
        doc.setStatus(status);
        LOGGER.info("Update document {} status: {}", doc.getFilename(), status);
        return repository.save(doc);
    }
}
