package com.aegisflow.api.reconciliation.matching;

import static org.assertj.core.api.Assertions.assertThat;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ExactReferenceMatchingStrategyTest {

    @Test
    void matchesExactReferenceWithinTolerance() {
        ReconciliationRule rule = new ReconciliationRule();
        rule.setPrimaryRecordType(FinancialRecordType.BANK_TRANSACTION);
        rule.setCandidateRecordType(FinancialRecordType.INVOICE_RECORD);
        rule.setAmountTolerance(new BigDecimal("0.01"));
        rule.setTimestampToleranceHours(48);
        rule.setRequireCurrencyMatch(true);

        FinancialTransaction bank = tx(FinancialRecordType.BANK_TRANSACTION, "INV-77", "100.00");
        FinancialTransaction invoice = tx(FinancialRecordType.INVOICE_RECORD, "INV-77", "100.00");

        assertThat(new ExactReferenceMatchingStrategy().evaluate(bank, invoice, rule)).isPresent();
    }

    private FinancialTransaction tx(FinancialRecordType type, String reference, String amount) {
        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setRecordType(type);
        transaction.setExternalReference(reference);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setCurrencyCode("USD");
        transaction.setTransactionTimestamp(Instant.parse("2026-05-25T10:00:00Z"));
        return transaction;
    }
}
