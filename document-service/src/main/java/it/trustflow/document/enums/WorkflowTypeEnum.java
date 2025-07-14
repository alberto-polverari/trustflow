package it.trustflow.document.enums;

public enum WorkflowTypeEnum {

    SEQUENZIALE("SEQUENZIALE"),
    PARALLELO("PARALLELO");

    private final String value;

    WorkflowTypeEnum(String value) {
        this.value = value;
    }
}
