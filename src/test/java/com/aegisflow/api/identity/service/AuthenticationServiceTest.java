package com.aegisflow.api.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aegisflow.api.common.security.CurrentUser;
import com.aegisflow.api.identity.domain.MembershipStatus;
import com.aegisflow.api.identity.domain.OrganizationMembership;
import com.aegisflow.api.identity.domain.Permission;
import com.aegisflow.api.identity.domain.Role;
import com.aegisflow.api.identity.domain.User;
import com.aegisflow.api.identity.dto.request.LoginRequest;
import com.aegisflow.api.identity.dto.response.AuthTokenResponse;
import com.aegisflow.api.identity.repository.OrganizationMembershipRepository;
import com.aegisflow.api.identity.repository.PermissionRepository;
import com.aegisflow.api.identity.repository.RefreshTokenRepository;
import com.aegisflow.api.identity.repository.RoleRepository;
import com.aegisflow.api.identity.repository.UserRepository;
import com.aegisflow.api.organizations.domain.Organization;
import com.aegisflow.api.organizations.repository.OrganizationRepository;
import com.aegisflow.api.security.IssuedJwtToken;
import com.aegisflow.api.security.JwtTokenService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private OrganizationMembershipRepository membershipRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private TokenHashService tokenHashService;
    @Mock private OrganizationSlugService slugService;
    @Mock private CurrentUserFactory currentUserFactory;
    @Mock private AuthenticationAuditService auditService;

    @InjectMocks private AuthenticationService authenticationService;

    @Test
    void loginIssuesOrganizationScopedTokens() {
        UUID userId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        User user = user(userId);
        Organization organization = organization(organizationId);
        OrganizationMembership membership = membership(user, organization);
        CurrentUser currentUser = new CurrentUser(userId, organizationId, user.getEmail(), List.of());

        when(userRepository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("valid-password", user.getPasswordHash())).thenReturn(true);
        when(membershipRepository.findByUserIdAndStatus(userId, MembershipStatus.ACTIVE)).thenReturn(List.of(membership));
        when(currentUserFactory.create(user, organizationId)).thenReturn(currentUser);
        when(jwtTokenService.issueAccessToken(currentUser)).thenReturn(new IssuedJwtToken("access.jwt", "access-id", Instant.now().plusSeconds(900)));
        when(jwtTokenService.issueRefreshToken(userId, organizationId)).thenReturn(new IssuedJwtToken("refresh.jwt", "refresh-id", Instant.now().plusSeconds(86400)));
        when(tokenHashService.sha256("refresh.jwt")).thenReturn("refresh-hash");
        when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(user)).thenReturn(user);

        AuthTokenResponse response = authenticationService.login(
                new LoginRequest(user.getEmail(), "valid-password", organizationId),
                new AuthRequestContext("127.0.0.1", "JUnit"));

        assertThat(response.accessToken()).isEqualTo("access.jwt");
        assertThat(response.refreshToken()).isEqualTo("refresh.jwt");
        assertThat(response.organizationId()).isEqualTo(organizationId);
    }

    private User user(UUID userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("finance.admin@enterprise.test");
        user.setPasswordHash("hashed-password");
        user.setFirstName("Finance");
        user.setLastName("Admin");
        return user;
    }

    private Organization organization(UUID organizationId) {
        Organization organization = new Organization();
        organization.setId(organizationId);
        organization.setDisplayName("Enterprise Finance");
        organization.setLegalName("Enterprise Finance Inc");
        organization.setSlug("enterprise-finance");
        return organization;
    }

    private OrganizationMembership membership(User user, Organization organization) {
        Permission permission = new Permission();
        permission.setCode("USER_ADMIN");
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setCode("ORG_OWNER");
        role.setPermissions(Set.of(permission));

        OrganizationMembership membership = new OrganizationMembership();
        membership.setUser(user);
        membership.setOrganization(organization);
        membership.setRole(role);
        membership.setStatus(MembershipStatus.ACTIVE);
        return membership;
    }
}
