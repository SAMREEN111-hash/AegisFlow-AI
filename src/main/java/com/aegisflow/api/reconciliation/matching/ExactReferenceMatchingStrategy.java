package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExactReferenceMatchingStrategy extends AbstractRuleAwareMatchingStrategy {

    @Override
    public Optional<MatchCandidate> evaluate(FinancialTransaction primary, FinancialTransaction candidate, ReconciliationRule rule) {
        if (!baseRuleEligible(primary, candidate, rule)) {
            return Optional.empty();
        }
        if (!primary.getExternalReference().equalsIgnoreCase(candidate.getExternalReference())) {
            return Optional.empty();
        }
        BigDecimal variance = variance(primary, candidate);
        if (variance.compareTo(rule.getAmountTolerance()) > 0) {
            return Optional.empty();
        }
        return Optional.of(new MatchCandidate(primary, candidate, new BigDecimal("1.0000"), variance,
                "EXACT_REFERENCE", "{\"reason\":\"Exact reference, currency, amount tolerance, and timestamp-window match\"}"));
    }
}
