package com.aegisflow.api.identity.domain;

import com.aegisflow.api.common.persistence.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens", schema = "identity")
public class RefreshToken extends BaseAuditableEntity {

    @Column(name = "token_id", nullable = false, unique = true, length = 100)
    private String tokenId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private RefreshTokenStatus status = RefreshTokenStatus.ACTIVE;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_token_id", length = 100)
    private String replacedByTokenId;

    @Column(name = "issued_ip", length = 100)
    private String issuedIp;

    @Column(name = "issued_user_agent", length = 1000)
    private String issuedUserAgent;

    public boolean isActive(Instant now) {
        return status == RefreshTokenStatus.ACTIVE && revokedAt == null && expiresAt.isAfter(now);
    }
}
