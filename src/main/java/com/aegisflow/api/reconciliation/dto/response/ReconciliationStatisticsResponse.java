package com.aegisflow.api.reconciliation.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ReconciliationStatisticsResponse(
        UUID jobId,
        int candidateCount,
        int matchedCount,
        int exceptionCount,
        BigDecimal matchRate
) {}
