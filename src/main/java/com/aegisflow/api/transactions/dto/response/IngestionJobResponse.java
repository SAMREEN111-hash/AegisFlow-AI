package com.aegisflow.api.transactions.dto.response;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.IngestionJobStatus;
import java.time.Instant;
import java.util.UUID;

public record IngestionJobResponse(
        UUID id,
        UUID organizationId,
        UUID sourceId,
        FinancialRecordType recordType,
        String sourceName,
        IngestionJobStatus status,
        String originalFilename,
        int totalRecords,
        int processedRecords,
        int failedRecords,
        int duplicateRecords,
        Instant startedAt,
        Instant completedAt,
        String failureReason
) {
}
