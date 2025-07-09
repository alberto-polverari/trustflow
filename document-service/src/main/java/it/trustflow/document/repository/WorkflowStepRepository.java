package it.trustflow.document.repository;

import it.trustflow.document.entity.WorkflowDefinition;
import it.trustflow.document.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {

    List<WorkflowStep> findByWorkflowDefinitionOrderByStepOrderAsc(WorkflowDefinition workflowDefinition);
}
