package com.aegisflow.api.transactions.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "financial_transactions", schema = "finance")
public class FinancialTransaction extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private TransactionSource source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingestion_job_id", nullable = false)
    private IngestionJob ingestionJob;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingestion_batch_id", nullable = false)
    private IngestionBatch ingestionBatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 50)
    private FinancialRecordType recordType;

    @Column(name = "external_reference", nullable = false, length = 255)
    private String externalReference;

    @Column(name = "counterparty_name", length = 255)
    private String counterpartyName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "transaction_timestamp", nullable = false)
    private Instant transactionTimestamp;

    @Column(name = "posting_timestamp")
    private Instant postingTimestamp;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private TransactionDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "reconciliation_status", nullable = false, length = 50)
    private ReconciliationStatus reconciliationStatus = ReconciliationStatus.UNRECONCILED;

    @Column(name = "duplicate_fingerprint", nullable = false, length = 128)
    private String duplicateFingerprint;

    @Column(name = "raw_payload", nullable = false, columnDefinition = "jsonb")
    private String rawPayload;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TransactionMetadata> metadata = new HashSet<>();
}
