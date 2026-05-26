package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.TransactionMetadata;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionMetadataRepository extends JpaRepository<TransactionMetadata, UUID> {
}
