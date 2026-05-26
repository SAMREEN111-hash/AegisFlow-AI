package com.aegisflow.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "aegisflow.security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String secret,
        @Positive long accessTokenTtlMinutes,
        @Positive long refreshTokenTtlDays
) {
}
