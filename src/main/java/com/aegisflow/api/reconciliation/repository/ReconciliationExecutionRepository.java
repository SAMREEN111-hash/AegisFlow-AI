package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationExecution;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationExecutionRepository extends JpaRepository<ReconciliationExecution, UUID> {
}
