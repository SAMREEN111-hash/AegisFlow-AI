package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ToleranceAmountMatchingStrategy extends AbstractRuleAwareMatchingStrategy {

    @Override
    public Optional<MatchCandidate> evaluate(FinancialTransaction primary, FinancialTransaction candidate, ReconciliationRule rule) {
        if (!baseRuleEligible(primary, candidate, rule)) {
            return Optional.empty();
        }
        BigDecimal variance = variance(primary, candidate);
        if (variance.compareTo(rule.getAmountTolerance()) > 0) {
            return Optional.empty();
        }
        BigDecimal denominator = primary.getAmount().max(BigDecimal.ONE);
        BigDecimal amountScore = BigDecimal.ONE.subtract(variance.divide(denominator, 4, RoundingMode.HALF_UP)).max(BigDecimal.ZERO);
        BigDecimal score = amountScore.multiply(new BigDecimal("0.8500"));
        return Optional.of(new MatchCandidate(primary, candidate, score, variance,
                "AMOUNT_TOLERANCE", "{\"reason\":\"Amount within configured tolerance and timestamp window\"}"));
    }
}
