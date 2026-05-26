package com.aegisflow.api.transactions.service;

import com.aegisflow.api.common.exception.ValidationException;
import com.aegisflow.api.transactions.domain.FinancialRecordType;
import com.aegisflow.api.transactions.domain.TransactionDirection;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionNormalizationService {

    private final TransactionFingerprintService fingerprintService;

    public NormalizedTransactionRecord normalize(UUID organizationId, FinancialRecordType recordType, ParsedCsvRecord csvRecord) {
        Map<String, String> values = csvRecord.values();
        String externalReference = required(values, "external_reference");
        BigDecimal amount = parseAmount(required(values, "amount"));
        String currencyCode = required(values, "currency_code").toUpperCase(Locale.ROOT);
        Instant transactionTimestamp = parseTimestamp(required(values, "transaction_timestamp"));
        Instant postingTimestamp = optional(values, "posting_timestamp") == null ? null : parseTimestamp(optional(values, "posting_timestamp"));
        TransactionDirection direction = TransactionDirection.valueOf(required(values, "direction").toUpperCase(Locale.ROOT));
        String fingerprint = fingerprintService.fingerprint(organizationId, recordType, externalReference, amount, currencyCode, transactionTimestamp);

        return new NormalizedTransactionRecord(
                recordType,
                externalReference,
                optional(values, "counterparty_name"),
                optional(values, "description"),
                transactionTimestamp,
                postingTimestamp,
                currencyCode,
                amount,
                direction,
                fingerprint,
                csvRecord.rawPayload(),
                values);
    }

    private String required(Map<String, String> values, String key) {
        String value = optional(values, key);
        if (value == null || value.isBlank()) {
            throw new ValidationException(key + " is required");
        }
        return value.trim();
    }

    private String optional(Map<String, String> values, String key) {
        return values.get(key);
    }

    private BigDecimal parseAmount(String value) {
        try {
            return new BigDecimal(value).abs();
        } catch (NumberFormatException exception) {
            throw new ValidationException("amount must be a valid decimal");
        }
    }

    private Instant parseTimestamp(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
            } catch (DateTimeParseException exception) {
                throw new ValidationException("timestamp must be ISO-8601 instant or ISO local date");
            }
        }
    }
}
