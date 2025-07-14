package it.trustflow.audit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Audit implements Serializable {
    private String ip;
    private String userId;
    private String tenantId;
    private String eventType; // es: "DOCUMENT_UPLOADED"
    private String eventDescription; // es: "Caricamto documento"
    private String eventMessage; // es: "Documento caricato con successo"
}
