package com.aegisflow.api.reconciliation.dto.response;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import java.math.BigDecimal;
import java.util.UUID;

public record ReconciliationRuleResponse(
        UUID id,
        String name,
        FinancialRecordType primaryRecordType,
        FinancialRecordType candidateRecordType,
        BigDecimal amountTolerance,
        int timestampToleranceHours,
        BigDecimal referenceSimilarityThreshold,
        BigDecimal autoMatchConfidenceThreshold,
        boolean requireCurrencyMatch,
        boolean active
) {}
