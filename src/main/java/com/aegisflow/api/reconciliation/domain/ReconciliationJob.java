package com.aegisflow.api.reconciliation.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reconciliation_jobs", schema = "reconciliation")
public class ReconciliationJob extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    private ReconciliationRule rule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReconciliationJobStatus status = ReconciliationJobStatus.PENDING;

    @Column(name = "name", nullable = false, length = 180)
    private String name;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "candidate_count", nullable = false)
    private int candidateCount;

    @Column(name = "matched_count", nullable = false)
    private int matchedCount;

    @Column(name = "exception_count", nullable = false)
    private int exceptionCount;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
}
