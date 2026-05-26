package com.aegisflow.api.identity.dto.response;

import java.util.UUID;

public record UserRegistrationResponse(
        UUID userId,
        UUID organizationId,
        String email,
        String organizationName
) {
}
