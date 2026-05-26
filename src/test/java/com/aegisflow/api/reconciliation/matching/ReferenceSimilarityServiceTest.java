package com.aegisflow.api.reconciliation.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReferenceSimilarityServiceTest {

    private final ReferenceSimilarityService service = new ReferenceSimilarityService();

    @Test
    void scoresNearReferencesHigherThanDifferentReferences() {
        assertThat(service.similarity("INV-10001", "INV10001"))
                .isGreaterThan(service.similarity("INV-10001", "PAY-44891"));
    }
}
