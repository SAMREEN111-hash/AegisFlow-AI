package com.aegisflow.api.transactions.dto.request;

import com.aegisflow.api.common.pagination.SortDirection;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.ReconciliationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;

public record TransactionSearchRequest(
        FinancialRecordType recordType,
        ReconciliationStatus reconciliationStatus,
        String currencyCode,
        Instant from,
        Instant to,
        @Min(0) Integer page,
        @Min(1) @Max(200) Integer size,
        String sortBy,
        SortDirection direction
) {
}
