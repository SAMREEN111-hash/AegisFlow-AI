package com.aegisflow.api.identity.repository;

import com.aegisflow.api.identity.domain.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenId(String tokenId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update RefreshToken token
               set token.status = com.aegisflow.api.identity.domain.RefreshTokenStatus.REVOKED,
                   token.revokedAt = CURRENT_TIMESTAMP
             where token.user.id = :userId
               and token.status = com.aegisflow.api.identity.domain.RefreshTokenStatus.ACTIVE
            """)
    int revokeActiveTokensForUser(UUID userId);
}
