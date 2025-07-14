package it.trustflow.document.repository;

import it.trustflow.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByTenantId(Long tenantId);

    Optional<Document> findByIdAndTenantId(Long id, Long tenantId);
}
