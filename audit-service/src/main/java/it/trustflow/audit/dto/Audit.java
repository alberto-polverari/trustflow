package it.trustflow.audit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Audit {
    private String userId;
    private String tenantId;
    private String eventType; // es: "DOCUMENT_UPLOADED"
    private String eventDescription; // es: "Caricamto documento"
    private String eventMessage; // es: "Documento caricato con successo"
}
