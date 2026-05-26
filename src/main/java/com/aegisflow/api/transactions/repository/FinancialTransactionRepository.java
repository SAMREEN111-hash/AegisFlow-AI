package com.aegisflow.api.transactions.repository;

import com.aegisflow.api.transactions.domain.FinancialTransaction;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.ReconciliationStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID>, JpaSpecificationExecutor<FinancialTransaction> {

    Optional<FinancialTransaction> findByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByOrganizationIdAndDuplicateFingerprint(UUID organizationId, String duplicateFingerprint);

    static Specification<FinancialTransaction> organizationEquals(UUID organizationId) {
        return (root, query, builder) -> builder.equal(root.get("organizationId"), organizationId);
    }

    static Specification<FinancialTransaction> statusEquals(ReconciliationStatus status) {
        return (root, query, builder) -> status == null ? builder.conjunction() : builder.equal(root.get("reconciliationStatus"), status);
    }

    static Specification<FinancialTransaction> recordTypeEquals(FinancialRecordType recordType) {
        return (root, query, builder) -> recordType == null ? builder.conjunction() : builder.equal(root.get("recordType"), recordType);
    }

    static Specification<FinancialTransaction> currencyEquals(String currencyCode) {
        return (root, query, builder) -> currencyCode == null ? builder.conjunction() : builder.equal(root.get("currencyCode"), currencyCode);
    }

    static Specification<FinancialTransaction> transactionTimestampFrom(Instant from) {
        return (root, query, builder) -> from == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("transactionTimestamp"), from);
    }

    static Specification<FinancialTransaction> transactionTimestampTo(Instant to) {
        return (root, query, builder) -> to == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("transactionTimestamp"), to);
    }
}
