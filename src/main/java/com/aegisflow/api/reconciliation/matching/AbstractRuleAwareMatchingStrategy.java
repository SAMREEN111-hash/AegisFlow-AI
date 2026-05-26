package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.math.BigDecimal;
import java.time.Duration;

abstract class AbstractRuleAwareMatchingStrategy implements MatchingStrategy {

    protected boolean baseRuleEligible(FinancialTransaction primary, FinancialTransaction candidate, ReconciliationRule rule) {
        if (rule.isRequireCurrencyMatch() && !primary.getCurrencyCode().equals(candidate.getCurrencyCode())) {
            return false;
        }
        if (primary.getRecordType() != rule.getPrimaryRecordType() || candidate.getRecordType() != rule.getCandidateRecordType()) {
            return false;
        }
        return Math.abs(Duration.between(primary.getTransactionTimestamp(), candidate.getTransactionTimestamp()).toHours())
                <= rule.getTimestampToleranceHours();
    }

    protected BigDecimal variance(FinancialTransaction primary, FinancialTransaction candidate) {
        return primary.getAmount().subtract(candidate.getAmount()).abs();
    }
}
