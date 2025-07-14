package it.trustflow.document.controller;

import it.trustflow.document.entity.Document;
import it.trustflow.document.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(
        @RequestParam("file") MultipartFile file,
        HttpServletRequest request
    ) {
        Document doc = documentService.upload(file, request);
        return ResponseEntity.ok(doc);
    }

    @PostMapping("/uploadMockFile")
    public ResponseEntity<Document> uploadMock(
        @RequestParam("filename") String filename,
        HttpServletRequest request
    ) {
        Document doc = documentService.uploadMock(filename, request);
        return ResponseEntity.ok(doc);
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {
        Document doc = documentService.findById(id).orElseThrow();

        FileSystemResource resource = new FileSystemResource(doc.getPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(doc.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(doc.getFilename()).build());

        return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Document> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Document updated = documentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
