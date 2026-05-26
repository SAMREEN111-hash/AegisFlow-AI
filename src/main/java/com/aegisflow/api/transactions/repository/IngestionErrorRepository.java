package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.IngestionError;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionErrorRepository extends JpaRepository<IngestionError, UUID> {

    Page<IngestionError> findByOrganizationIdAndJobId(UUID organizationId, UUID jobId, Pageable pageable);
}
