package com.aegisflow.api.transactions.dto.response;

import java.util.UUID;

public record CsvUploadResponse(
        UUID ingestionJobId,
        int totalRecords,
        int processedRecords,
        int failedRecords,
        int duplicateRecords
) {
}
