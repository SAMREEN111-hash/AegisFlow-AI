package com.aegisflow.api.security;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenClaims(
        String tokenId,
        UUID userId,
        UUID organizationId,
        Instant expiresAt
) {
}
