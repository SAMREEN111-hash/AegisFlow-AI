package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.util.Optional;

public interface MatchingStrategy {
    Optional<MatchCandidate> evaluate(FinancialTransaction primary, FinancialTransaction candidate, ReconciliationRule rule);
}
