package com.aegisflow.api.reconciliation.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reconciliation_matches", schema = "reconciliation")
public class ReconciliationMatch extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private ReconciliationJob job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "primary_transaction_id", nullable = false)
    private FinancialTransaction primaryTransaction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_transaction_id", nullable = false)
    private FinancialTransaction candidateTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type", nullable = false, length = 50)
    private ReconciliationMatchType matchType = ReconciliationMatchType.ONE_TO_ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReconciliationMatchStatus status;

    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal confidenceScore;

    @Column(name = "amount_variance", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountVariance;

    @Column(name = "matched_by_strategy", nullable = false, length = 120)
    private String matchedByStrategy;

    @Column(name = "explanation", nullable = false, columnDefinition = "jsonb")
    private String explanation;
}
