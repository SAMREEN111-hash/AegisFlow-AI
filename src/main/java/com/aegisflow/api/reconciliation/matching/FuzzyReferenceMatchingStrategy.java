package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuzzyReferenceMatchingStrategy extends AbstractRuleAwareMatchingStrategy {

    private final ReferenceSimilarityService referenceSimilarityService;

    @Override
    public Optional<MatchCandidate> evaluate(FinancialTransaction primary, FinancialTransaction candidate, ReconciliationRule rule) {
        if (!baseRuleEligible(primary, candidate, rule)) {
            return Optional.empty();
        }
        BigDecimal similarity = referenceSimilarityService.similarity(primary.getExternalReference(), candidate.getExternalReference());
        if (similarity.compareTo(rule.getReferenceSimilarityThreshold()) < 0) {
            return Optional.empty();
        }
        BigDecimal variance = variance(primary, candidate);
        if (variance.compareTo(rule.getAmountTolerance()) > 0) {
            return Optional.empty();
        }
        BigDecimal score = similarity.multiply(new BigDecimal("0.9200"));
        return Optional.of(new MatchCandidate(primary, candidate, score, variance,
                "FUZZY_REFERENCE", "{\"reason\":\"Reference similarity exceeded configured threshold\"}"));
    }
}
