package it.trustflow.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class AuditLog implements Serializable {
    private String ip;
    private String userId;
    private String tenantId;
    private String eventType;
    private String eventDescription;
    private String eventMessage;
}
