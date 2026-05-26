package com.aegisflow.api.transactions.domain;

public enum IngestionJobStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    COMPLETED_WITH_ERRORS,
    FAILED
}
