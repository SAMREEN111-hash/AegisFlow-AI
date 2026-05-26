package com.aegisflow.api.reconciliation.dto.request;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateReconciliationRuleRequest(
        @NotBlank String name,
        @NotNull FinancialRecordType primaryRecordType,
        @NotNull FinancialRecordType candidateRecordType,
        @NotNull @DecimalMin("0.0000") BigDecimal amountTolerance,
        @Min(0) @Max(720) int timestampToleranceHours,
        @NotNull @DecimalMin("0.0000") BigDecimal referenceSimilarityThreshold,
        @NotNull @DecimalMin("0.0000") BigDecimal autoMatchConfidenceThreshold,
        boolean requireCurrencyMatch
) {
}
