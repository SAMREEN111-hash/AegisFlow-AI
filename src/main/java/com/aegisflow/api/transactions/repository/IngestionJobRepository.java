package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.IngestionJob;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionJobRepository extends JpaRepository<IngestionJob, UUID> {

    Optional<IngestionJob> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
