package com.aegisflow.api.transactions.domain;

public enum ReconciliationStatus {
    UNRECONCILED,
    MATCHED,
    PARTIALLY_MATCHED,
    EXCEPTION,
    MANUALLY_RESOLVED
}
