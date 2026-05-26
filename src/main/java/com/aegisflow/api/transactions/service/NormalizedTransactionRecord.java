package com.aegisflow.api.transactions.service;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.TransactionDirection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record NormalizedTransactionRecord(
        FinancialRecordType recordType,
        String externalReference,
        String counterpartyName,
        String description,
        Instant transactionTimestamp,
        Instant postingTimestamp,
        String currencyCode,
        BigDecimal amount,
        TransactionDirection direction,
        String duplicateFingerprint,
        String rawPayload,
        Map<String, String> metadata
) {
}
