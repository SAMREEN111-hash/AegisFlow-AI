package com.aegisflow.api.identity.domain;

public enum RefreshTokenStatus {
    ACTIVE,
    ROTATED,
    REVOKED,
    EXPIRED
}
