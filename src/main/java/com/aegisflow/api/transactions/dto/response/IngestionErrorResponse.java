package com.aegisflow.api.transactions.dto.response;

import java.util.UUID;

public record IngestionErrorResponse(
        UUID id,
        UUID jobId,
        Long rowNumber,
        String errorCode,
        String errorMessage,
        String rawPayload
) {
}
