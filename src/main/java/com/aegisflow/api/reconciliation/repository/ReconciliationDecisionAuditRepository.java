package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationDecisionAudit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationDecisionAuditRepository extends JpaRepository<ReconciliationDecisionAudit, UUID> {
}
