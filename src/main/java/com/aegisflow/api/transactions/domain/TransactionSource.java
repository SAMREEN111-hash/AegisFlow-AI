package com.aegisflow.api.transactions.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "transaction_sources", schema = "finance")
public class TransactionSource extends BaseAuditableEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", nullable = false, length = 50)
    private FinancialRecordType recordType;

    @Column(name = "source_name", nullable = false, length = 150)
    private String sourceName;

    @Column(name = "provider_name", length = 150)
    private String providerName;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
