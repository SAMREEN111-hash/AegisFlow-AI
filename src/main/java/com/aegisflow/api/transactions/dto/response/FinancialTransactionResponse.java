package com.aegisflow.api.transactions.dto.response;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.ReconciliationStatus;
import com.aegisflow.api.transactions.domain.TransactionDirection;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record FinancialTransactionResponse(
        UUID id,
        UUID organizationId,
        UUID sourceId,
        FinancialRecordType recordType,
        String externalReference,
        String counterpartyName,
        String description,
        Instant transactionTimestamp,
        Instant postingTimestamp,
        String currencyCode,
        BigDecimal amount,
        TransactionDirection direction,
        ReconciliationStatus reconciliationStatus,
        Map<String, String> metadata
) {
}
