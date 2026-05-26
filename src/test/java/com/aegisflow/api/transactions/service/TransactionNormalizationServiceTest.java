package com.aegisflow.api.transactions.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.aegisflow.api.identity.service.TokenHashService;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.TransactionDirection;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionNormalizationServiceTest {

    private final TransactionNormalizationService normalizationService =
            new TransactionNormalizationService(new TransactionFingerprintService(new TokenHashService()));

    @Test
    void normalizesCsvRecordIntoCanonicalTransactionShape() {
        ParsedCsvRecord record = new ParsedCsvRecord(2, Map.of(
                "external_reference", "BNK-1001",
                "amount", "-125.50",
                "currency_code", "usd",
                "transaction_timestamp", "2026-05-25T10:15:30Z",
                "direction", "debit",
                "counterparty_name", "Acme Supplies",
                "description", "Vendor payment"
        ), "{\"external_reference\":\"BNK-1001\"}");

        NormalizedTransactionRecord normalized = normalizationService.normalize(UUID.randomUUID(), FinancialRecordType.BANK_TRANSACTION, record);

        assertThat(normalized.externalReference()).isEqualTo("BNK-1001");
        assertThat(normalized.amount()).isEqualByComparingTo(new BigDecimal("125.50"));
        assertThat(normalized.currencyCode()).isEqualTo("USD");
        assertThat(normalized.direction()).isEqualTo(TransactionDirection.DEBIT);
        assertThat(normalized.duplicateFingerprint()).isNotBlank();
    }
}
