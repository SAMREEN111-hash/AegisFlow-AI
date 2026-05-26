package com.aegisflow.api.reconciliation.dto.response;

import com.aegisflow.api.reconciliation.domain.ReconciliationExceptionStatus;
import com.aegisflow.api.reconciliation.domain.ReconciliationExceptionType;
import java.util.UUID;

public record ReconciliationExceptionResponse(
        UUID id,
        UUID jobId,
        UUID transactionId,
        ReconciliationExceptionType exceptionType,
        ReconciliationExceptionStatus status,
        String reason
) {}
