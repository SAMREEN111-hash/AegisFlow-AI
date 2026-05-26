package com.aegisflow.api.security;

import com.aegisflow.api.common.exception.AuthenticationFailedException;
import com.aegisflow.api.common.security.CurrentUser;
import com.aegisflow.api.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final String ORGANIZATION_ID = "organizationId";
    private static final String EMAIL = "email";
    private static final String AUTHORITIES = "authorities";
    private static final String TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public IssuedJwtToken issueAccessToken(CurrentUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.accessTokenTtlMinutes(), ChronoUnit.MINUTES);
        List<String> authorities = user.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String tokenId = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .issuer(jwtProperties.issuer())
                .id(tokenId)
                .subject(user.id().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(TOKEN_TYPE, ACCESS_TOKEN)
                .claim(ORGANIZATION_ID, user.organizationId().toString())
                .claim(EMAIL, user.email())
                .claim(AUTHORITIES, authorities)
                .signWith(signingKey)
                .compact();
        return new IssuedJwtToken(token, tokenId, expiresAt);
    }

    public IssuedJwtToken issueRefreshToken(UUID userId, UUID organizationId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.refreshTokenTtlDays(), ChronoUnit.DAYS);
        String tokenId = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .issuer(jwtProperties.issuer())
                .id(tokenId)
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim(TOKEN_TYPE, REFRESH_TOKEN)
                .claim(ORGANIZATION_ID, organizationId.toString())
                .signWith(signingKey)
                .compact();
        return new IssuedJwtToken(token, tokenId, expiresAt);
    }

    public CurrentUser parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(jwtProperties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            requireTokenType(claims, ACCESS_TOKEN);
            UUID userId = UUID.fromString(claims.getSubject());
            UUID organizationId = UUID.fromString(claims.get(ORGANIZATION_ID, String.class));
            String email = claims.get(EMAIL, String.class);
            Collection<GrantedAuthority> authorities = extractAuthorities(claims);

            return new CurrentUser(userId, organizationId, email, authorities);
        } catch (RuntimeException exception) {
            throw new AuthenticationFailedException("Invalid or expired access token");
        }
    }

    public RefreshTokenClaims parseRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(jwtProperties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            requireTokenType(claims, REFRESH_TOKEN);
            return new RefreshTokenClaims(
                    claims.getId(),
                    UUID.fromString(claims.getSubject()),
                    UUID.fromString(claims.get(ORGANIZATION_ID, String.class)),
                    claims.getExpiration().toInstant());
        } catch (RuntimeException exception) {
            throw new AuthenticationFailedException("Invalid or expired refresh token");
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        Object value = claims.get(AUTHORITIES);
        if (!(value instanceof List<?> rawAuthorities)) {
            return List.of();
        }
        return rawAuthorities.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }

    private void requireTokenType(Claims claims, String expectedType) {
        String tokenType = claims.get(TOKEN_TYPE, String.class);
        if (!expectedType.equals(tokenType)) {
            throw new AuthenticationFailedException("Invalid token type");
        }
    }
}
