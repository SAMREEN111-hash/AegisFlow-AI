package com.aegisflow.api.identity.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt,
        UUID userId,
        UUID organizationId,
        String email,
        List<String> permissions,
        List<OrganizationMembershipResponse> memberships
) {
}
