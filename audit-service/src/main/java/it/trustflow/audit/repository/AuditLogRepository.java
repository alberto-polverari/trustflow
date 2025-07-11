package it.trustflow.audit.repository;

import it.trustflow.audit.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository  extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByUserId(String userId);
}