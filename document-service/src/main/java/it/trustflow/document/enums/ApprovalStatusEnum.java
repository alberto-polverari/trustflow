package it.trustflow.document.enums;

public enum ApprovalStatusEnum {

    IN_ATTESA("IN_ATTESA"),
    RIFIUTATO("RIFIUTATO"),
    APPROVATO("APPROVATO");

    private final String value;

    ApprovalStatusEnum(String value) {
        this.value = value;
    }
}
