package it.trustflow.document.entity;

import it.trustflow.document.entity.WorkflowInstance;
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
public class Approval implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String approverId;
    private String status; // IN_ATTESA, APPROVATO, RIFIUTATO
    private String comment;
    private LocalDateTime approvedAt;
    private int stepOrder;

    @ManyToOne
    private WorkflowInstance workflowInstance;
}
