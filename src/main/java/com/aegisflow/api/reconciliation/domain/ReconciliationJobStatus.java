package com.aegisflow.api.reconciliation.domain;

public enum ReconciliationJobStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    COMPLETED_WITH_EXCEPTIONS,
    FAILED
}
