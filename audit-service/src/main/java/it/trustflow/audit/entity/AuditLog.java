package it.trustflow.audit.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String ip;
    private String userId;
    private String tenantId;
    private String eventType; // es: "DOCUMENT_UPLOADED"
    private String eventDescription; // es: "Caricamto documento"
    private String eventMessage; // es: "Documento caricato con successo"
    private LocalDateTime eventDate = LocalDateTime.now();

}

