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
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reconciliation_exceptions", schema = "reconciliation")
public class ReconciliationException extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private ReconciliationJob job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private FinancialTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_type", nullable = false, length = 80)
    private ReconciliationExceptionType exceptionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReconciliationExceptionStatus status = ReconciliationExceptionStatus.OPEN;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;
}
