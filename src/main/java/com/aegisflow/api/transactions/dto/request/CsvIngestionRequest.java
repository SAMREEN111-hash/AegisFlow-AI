package com.aegisflow.api.transactions.dto.request;

import com.aegisflow.api.transactions.domain.FinancialRecordType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CsvIngestionRequest(
        @NotNull FinancialRecordType recordType,
        @NotBlank String sourceName,
        String providerName
) {
}
