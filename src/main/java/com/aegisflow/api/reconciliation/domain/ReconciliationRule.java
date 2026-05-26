package com.aegisflow.api.reconciliation.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reconciliation_rules", schema = "reconciliation")
public class ReconciliationRule extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "name", nullable = false, length = 180)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_record_type", nullable = false, length = 50)
    private FinancialRecordType primaryRecordType;

    @Enumerated(EnumType.STRING)
    @Column(name = "candidate_record_type", nullable = false, length = 50)
    private FinancialRecordType candidateRecordType;

    @Column(name = "amount_tolerance", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountTolerance = BigDecimal.ZERO;

    @Column(name = "timestamp_tolerance_hours", nullable = false)
    private int timestampToleranceHours = 24;

    @Column(name = "reference_similarity_threshold", nullable = false, precision = 5, scale = 4)
    private BigDecimal referenceSimilarityThreshold = new BigDecimal("0.8500");

    @Column(name = "auto_match_confidence_threshold", nullable = false, precision = 5, scale = 4)
    private BigDecimal autoMatchConfidenceThreshold = new BigDecimal("0.9000");

    @Column(name = "require_currency_match", nullable = false)
    private boolean requireCurrencyMatch = true;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
