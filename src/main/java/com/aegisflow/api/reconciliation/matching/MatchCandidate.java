package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.math.BigDecimal;

public record MatchCandidate(
        FinancialTransaction primary,
        FinancialTransaction candidate,
        BigDecimal confidenceScore,
        BigDecimal amountVariance,
        String strategy,
        String explanation
) {
}
