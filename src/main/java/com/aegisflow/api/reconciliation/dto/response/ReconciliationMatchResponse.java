package com.aegisflow.api.reconciliation.dto.response;

import com.aegisflow.api.reconciliation.domain.ReconciliationMatchStatus;
import com.aegisflow.api.reconciliation.domain.ReconciliationMatchType;
import java.math.BigDecimal;
import java.util.UUID;

public record ReconciliationMatchResponse(
        UUID id,
        UUID primaryTransactionId,
        UUID candidateTransactionId,
        ReconciliationMatchType matchType,
        ReconciliationMatchStatus status,
        BigDecimal confidenceScore,
        BigDecimal amountVariance,
        String matchedByStrategy,
        String explanation
) {}
