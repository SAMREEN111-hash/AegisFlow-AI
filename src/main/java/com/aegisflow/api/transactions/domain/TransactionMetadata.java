package com.aegisflow.api.transactions.domain;

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
@Table(name = "transaction_metadata", schema = "finance")
public class TransactionMetadata extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private FinancialTransaction transaction;

    @Column(name = "metadata_key", nullable = false, length = 150)
    private String metadataKey;

    @Column(name = "metadata_value", length = 2000)
    private String metadataValue;
}
