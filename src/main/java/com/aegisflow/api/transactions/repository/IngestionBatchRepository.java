package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.IngestionBatch;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionBatchRepository extends JpaRepository<IngestionBatch, UUID> {
}
