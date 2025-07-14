package it.trustflow.document.enums;

public enum DocumentStatusEnum {
    IN_ATTESA("IN_ATTESA"),
    IN_REVISIONE("IN_REVISIONE"),
    APPROVATO("APPROVATO"),
    REVISIONE_UTENTE("REVISIONE_UTENTE");

    private final String value;

    DocumentStatusEnum(String value) {
        this.value = value;
    }
}
