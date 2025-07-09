package it.trustflow.document.service;

import it.trustflow.document.entity.Document;
import it.trustflow.document.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repository;

    public Document upload(MultipartFile file, Long ownerId, Long tenantId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "_" + originalFilename;

        Document doc = Document.builder()
        .filename(newFilename)
        .contentType(file.getContentType())
        .ownerId(ownerId)
        .tenantId(tenantId)
        .dataInserimento(LocalDateTime.now())
        .dataModifica(LocalDateTime.now())
        .status("IN_ATTESA")
        .path("")
        .build();

        return repository.save(doc);
    }

    public Optional<Document> findById(Long id) {
        return repository.findById(id);
    }

    public Document updateStatus(Long id, String status) {
        Document doc = repository.findById(id).orElseThrow();
        doc.setStatus(status);
        return repository.save(doc);
    }
}
