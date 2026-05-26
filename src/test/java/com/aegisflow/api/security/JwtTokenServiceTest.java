package com.aegisflow.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.aegisflow.api.common.security.CurrentUser;
import com.aegisflow.api.config.JwtProperties;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtTokenServiceTest {

    private final JwtTokenService jwtTokenService = new JwtTokenService(new JwtProperties(
            "aegisflow-ai-test",
            "test_secret_that_is_long_enough_for_hmac_sha_256",
            15,
            7));

    @Test
    void issuedAccessTokenCanBeParsedIntoCurrentUser() {
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("RECONCILIATION_READ"));
        CurrentUser user = new CurrentUser(userId, organizationId, "finance.ops@enterprise.test", authorities);

        IssuedJwtToken token = jwtTokenService.issueAccessToken(user);
        CurrentUser parsedUser = jwtTokenService.parseAccessToken(token.token());

        assertThat(parsedUser.id()).isEqualTo(userId);
        assertThat(parsedUser.organizationId()).isEqualTo(organizationId);
        assertThat(parsedUser.email()).isEqualTo("finance.ops@enterprise.test");
        assertThat(parsedUser.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("RECONCILIATION_READ");
    }
}
