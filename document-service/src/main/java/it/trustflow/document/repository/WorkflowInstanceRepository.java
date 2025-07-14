package it.trustflow.document.repository;

import it.trustflow.document.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

    Optional<WorkflowInstance>  findByDocumentIdAndTenantId(Long documentId, Long tenantId);
}
