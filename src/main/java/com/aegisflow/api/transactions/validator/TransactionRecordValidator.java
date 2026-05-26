package com.aegisflow.api.transactions.validator;

import com.aegisflow.api.common.exception.ValidationException;
import com.aegisflow.api.transactions.service.NormalizedTransactionRecord;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TransactionRecordValidator {

    public void validate(NormalizedTransactionRecord record) {
        if (!StringUtils.hasText(record.externalReference())) {
            throw new ValidationException("external_reference is required");
        }
        if (record.transactionTimestamp() == null) {
            throw new ValidationException("transaction_timestamp is required");
        }
        if (record.transactionTimestamp().isAfter(Instant.now().plus(1, ChronoUnit.DAYS))) {
            throw new ValidationException("transaction_timestamp cannot be in the future");
        }
        if (record.amount() == null || record.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("amount must be greater than zero");
        }
        try {
            Currency.getInstance(record.currencyCode());
        } catch (RuntimeException exception) {
            throw new ValidationException("currency_code must be a valid ISO-4217 code");
        }
        if (record.direction() == null) {
            throw new ValidationException("direction is required");
        }
    }
}
