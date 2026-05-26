package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.TransactionSource;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionSourceRepository extends JpaRepository<TransactionSource, UUID> {

    Optional<TransactionSource> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<TransactionSource> findByOrganizationIdAndRecordTypeAndSourceName(UUID organizationId, FinancialRecordType recordType, String sourceName);
}
