package com.aegisflow.api.identity.policy;

import java.util.Arrays;
import java.util.List;

public enum IdentityPermission {
    TRANSACTION_READ,
    TRANSACTION_INGEST,
    RECONCILIATION_READ,
    RECONCILIATION_EXECUTE,
    RECONCILIATION_RUN,
    RECONCILIATION_OVERRIDE,
    RECONCILIATION_EXCEPTION_REVIEW,
    APPROVAL_APPROVE,
    AUDIT_READ,
    USER_ADMIN;

    public static List<String> allCodes() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }
}
