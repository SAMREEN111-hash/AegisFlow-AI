package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationJob;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationJobRepository extends JpaRepository<ReconciliationJob, UUID> {
    Optional<ReconciliationJob> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Page<ReconciliationJob> findByOrganizationId(UUID organizationId, Pageable pageable);
}
