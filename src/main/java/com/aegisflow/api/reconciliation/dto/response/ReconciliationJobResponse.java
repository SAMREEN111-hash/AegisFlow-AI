package com.aegisflow.api.reconciliation.dto.response;

import com.aegisflow.api.reconciliation.domain.ReconciliationJobStatus;
import java.time.Instant;
import java.util.UUID;

public record ReconciliationJobResponse(
        UUID id,
        UUID ruleId,
        String name,
        ReconciliationJobStatus status,
        int candidateCount,
        int matchedCount,
        int exceptionCount,
        Instant startedAt,
        Instant completedAt,
        String failureReason
) {}
