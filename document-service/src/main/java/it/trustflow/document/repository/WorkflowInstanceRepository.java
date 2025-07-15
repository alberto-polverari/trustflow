package it.trustflow.document.repository;

import it.trustflow.document.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {

    Optional<WorkflowInstance> findByIdAndTenantId(Long instanceId, Long tenantId);

    List<WorkflowInstance> findByTenantId(Long tenantId);

    Optional<WorkflowInstance> findByDocumentIdAndTenantId(Long documentId, Long tenantId);
}
