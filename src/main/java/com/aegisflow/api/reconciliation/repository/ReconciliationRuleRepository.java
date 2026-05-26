package com.aegisflow.api.reconciliation.repository;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReconciliationRuleRepository extends JpaRepository<ReconciliationRule, UUID> {
    Optional<ReconciliationRule> findByIdAndOrganizationId(UUID id, UUID organizationId);
    List<ReconciliationRule> findByOrganizationIdAndActiveTrue(UUID organizationId);
}
