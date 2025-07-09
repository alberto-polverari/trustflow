package it.trustflow.document.repository;

import it.trustflow.document.entity.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, Long> {

    // findByTenantId
    Optional<WorkflowDefinition> findByTenantId(Long tenantId);
}
