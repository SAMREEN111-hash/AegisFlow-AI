package com.aegisflow.api.reconciliation.domain;

public enum ReconciliationExceptionType {
    NO_CANDIDATE_FOUND,
    AMOUNT_VARIANCE,
    CURRENCY_MISMATCH,
    TIMESTAMP_OUTSIDE_WINDOW,
    LOW_CONFIDENCE,
    DUPLICATE_CANDIDATES
}
