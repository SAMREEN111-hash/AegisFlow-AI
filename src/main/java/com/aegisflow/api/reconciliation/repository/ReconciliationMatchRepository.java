package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationMatch;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationMatchRepository extends JpaRepository<ReconciliationMatch, UUID> {
    Page<ReconciliationMatch> findByOrganizationIdAndJobId(UUID organizationId, UUID jobId, Pageable pageable);
}
