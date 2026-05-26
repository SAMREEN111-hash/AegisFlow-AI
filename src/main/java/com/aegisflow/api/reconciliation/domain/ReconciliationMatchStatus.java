package com.aegisflow.api.reconciliation.domain;

public enum ReconciliationMatchStatus {
    AUTO_MATCHED,
    PARTIAL_MATCHED,
    PENDING_REVIEW,
    MANUALLY_OVERRIDDEN,
    REJECTED
}
