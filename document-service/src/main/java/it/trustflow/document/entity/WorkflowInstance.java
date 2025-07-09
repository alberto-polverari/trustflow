package it.trustflow.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowInstance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @JoinColumn(name = "document_id")
//    private Document document;
    private Long documentId;
    private Long tenantId;

    @ManyToOne
    private WorkflowDefinition workflowDefinition;

    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
