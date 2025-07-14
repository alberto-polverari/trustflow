package it.trustflow.document.enums;

public enum WorkflowStatusEnum {
    IN_ATTESA("IN_ATTESA"),
    IN_CORSO("IN_CORSO"),
    APPROVATO("APPROVATO"),
    RIFIUTATO("RIFIUTATO");

    private final String value;

    WorkflowStatusEnum(String value) {
        this.value = value;
    }
}
