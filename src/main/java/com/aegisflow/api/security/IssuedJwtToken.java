package com.aegisflow.api.security;

import java.time.Instant;

public record IssuedJwtToken(String token, String tokenId, Instant expiresAt) {
}
