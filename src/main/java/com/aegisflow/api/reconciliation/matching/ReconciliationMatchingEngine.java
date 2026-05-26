package com.aegisflow.api.reconciliation.matching;

import com.aegisflow.api.reconciliation.domain.ReconciliationRule;
import com.aegisflow.api.transactions.domain.FinancialTransaction;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReconciliationMatchingEngine {

    private final List<MatchingStrategy> strategies;

    public Optional<MatchCandidate> findBestMatch(FinancialTransaction primary, List<FinancialTransaction> candidates, ReconciliationRule rule) {
        return candidates.stream()
                .flatMap(candidate -> strategies.stream().map(strategy -> strategy.evaluate(primary, candidate, rule)))
                .flatMap(Optional::stream)
                .max(Comparator.comparing(MatchCandidate::confidenceScore));
    }
}
