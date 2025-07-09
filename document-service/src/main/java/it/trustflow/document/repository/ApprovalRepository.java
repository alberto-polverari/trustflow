package it.trustflow.document.repository;

import it.trustflow.document.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByWorkflowInstanceId(Long workflowInstanceId);

    Optional<Approval> findByWorkflowInstanceIdAndApproverId(Long workflowInstanceId, String approverId);
}
