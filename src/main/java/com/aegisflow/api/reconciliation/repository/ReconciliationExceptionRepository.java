package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationException;
import com.aegisflow.api.reconciliation.domain.ReconciliationExceptionStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationExceptionRepository extends JpaRepository<ReconciliationException, UUID> {
    Page<ReconciliationException> findByOrganizationIdAndStatus(UUID organizationId, ReconciliationExceptionStatus status, Pageable pageable);
    Page<ReconciliationException> findByOrganizationIdAndJobId(UUID organizationId, UUID jobId, Pageable pageable);
}
