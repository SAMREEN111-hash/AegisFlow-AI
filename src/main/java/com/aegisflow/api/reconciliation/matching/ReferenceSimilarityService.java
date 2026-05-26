package com.aegisflow.api.reconciliation.matching;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class ReferenceSimilarityService {

    public BigDecimal similarity(String left, String right) {
        String a = normalize(left);
        String b = normalize(right);
        if (a.isBlank() || b.isBlank()) {
            return BigDecimal.ZERO;
        }
        int distance = levenshtein(a, b);
        int max = Math.max(a.length(), b.length());
        return BigDecimal.ONE.subtract(BigDecimal.valueOf(distance).divide(BigDecimal.valueOf(max), 4, RoundingMode.HALF_UP));
    }

    private String normalize(String value) {
        return value == null ? "" : value.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int northwest = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int current = Math.min(Math.min(costs[j] + 1, costs[j - 1] + 1), northwest + cost);
                northwest = costs[j];
                costs[j] = current;
            }
        }
        return costs[b.length()];
    }
}
