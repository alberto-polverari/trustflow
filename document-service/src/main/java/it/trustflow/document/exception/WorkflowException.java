package it.trustflow.document.exception;

import lombok.Getter;

public class WorkflowException extends RuntimeException {

  public WorkflowException(String message) {
    super(message);
  }

}
