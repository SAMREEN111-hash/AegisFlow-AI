package com.aegisflow.api.reconciliation.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "reconciliation_decision_audit", schema = "reconciliation")
public class ReconciliationDecisionAudit extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private ReconciliationJob job;

    @Column(name = "decision_type", nullable = false, length = 100)
    private String decisionType;

    @Column(name = "decision_payload", nullable = false, columnDefinition = "jsonb")
    private String decisionPayload;
}
