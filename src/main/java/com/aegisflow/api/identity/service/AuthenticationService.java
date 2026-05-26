package com.aegisflow.api.identity.service;

import com.aegisflow.api.common.exception.BusinessException;
import com.aegisflow.api.common.exception.AuthenticationFailedException;
import com.aegisflow.api.common.exception.UnauthorizedActionException;
import com.aegisflow.api.common.security.CurrentUser;
import com.aegisflow.api.identity.domain.MembershipStatus;
import com.aegisflow.api.identity.domain.OrganizationMembership;
import com.aegisflow.api.identity.domain.Permission;
import com.aegisflow.api.identity.domain.RefreshToken;
import com.aegisflow.api.identity.domain.RefreshTokenStatus;
import com.aegisflow.api.identity.domain.Role;
import com.aegisflow.api.identity.domain.User;
import com.aegisflow.api.identity.dto.request.LoginRequest;
import com.aegisflow.api.identity.dto.request.RegisterUserRequest;
import com.aegisflow.api.identity.dto.response.AuthTokenResponse;
import com.aegisflow.api.identity.dto.response.LogoutResponse;
import com.aegisflow.api.identity.dto.response.OrganizationMembershipResponse;
import com.aegisflow.api.identity.dto.response.UserRegistrationResponse;
import com.aegisflow.api.identity.policy.IdentityPermission;
import com.aegisflow.api.identity.repository.OrganizationMembershipRepository;
import com.aegisflow.api.identity.repository.PermissionRepository;
import com.aegisflow.api.identity.repository.RefreshTokenRepository;
import com.aegisflow.api.identity.repository.RoleRepository;
import com.aegisflow.api.identity.repository.UserRepository;
import com.aegisflow.api.organizations.domain.Organization;
import com.aegisflow.api.organizations.repository.OrganizationRepository;
import com.aegisflow.api.security.IssuedJwtToken;
import com.aegisflow.api.security.JwtTokenService;
import com.aegisflow.api.security.RefreshTokenClaims;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String OWNER_ROLE_CODE = "ORG_OWNER";

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final OrganizationMembershipRepository membershipRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final TokenHashService tokenHashService;
    private final OrganizationSlugService slugService;
    private final CurrentUserFactory currentUserFactory;
    private final AuthenticationAuditService auditService;

    @Transactional
    public UserRegistrationResponse register(RegisterUserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("EMAIL_ALREADY_REGISTERED", "Email is already registered");
        }

        Organization organization = new Organization();
        organization.setLegalName(request.organizationName().trim());
        organization.setDisplayName(request.organizationName().trim());
        organization.setSlug(slugService.createUniqueSlug(request.organizationName()));
        organization = organizationRepository.save(organization);

        User user = new User();
        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        Role ownerRole = createOwnerRole(organization.getId());
        OrganizationMembership membership = new OrganizationMembership();
        membership.setUser(user);
        membership.setOrganization(organization);
        membership.setRole(ownerRole);
        membership.setStatus(MembershipStatus.ACTIVE);
        membershipRepository.save(membership);

        auditService.registrationSucceeded(user.getId(), organization.getId());
        return new UserRegistrationResponse(user.getId(), organization.getId(), user.getEmail(), organization.getDisplayName());
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request, AuthRequestContext context) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> invalidCredentials(email));

        Instant now = Instant.now();
        if (!user.canAuthenticate(now) || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);
            throw invalidCredentials(email);
        }

        List<OrganizationMembership> memberships = membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.ACTIVE);
        UUID organizationId = resolveOrganizationId(request.organizationId(), memberships);

        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(now);
        userRepository.save(user);

        AuthTokenResponse response = issueTokenResponse(user, organizationId, memberships, context);
        auditService.loginSucceeded(user.getId(), organizationId);
        return response;
    }

    @Transactional
    public AuthTokenResponse refresh(String refreshTokenValue, AuthRequestContext context) {
        RefreshTokenClaims claims = jwtTokenService.parseRefreshToken(refreshTokenValue);
        RefreshToken existingToken = refreshTokenRepository.findByTokenId(claims.tokenId())
                .orElseThrow(() -> new AuthenticationFailedException("Refresh token is not recognized"));

        String presentedHash = tokenHashService.sha256(refreshTokenValue);
        Instant now = Instant.now();
        if (!existingToken.getTokenHash().equals(presentedHash) || !existingToken.isActive(now)) {
            throw new AuthenticationFailedException("Refresh token is invalid or expired");
        }

        User user = existingToken.getUser();
        CurrentUser currentUser = currentUserFactory.create(user, existingToken.getOrganizationId());
        IssuedJwtToken accessToken = jwtTokenService.issueAccessToken(currentUser);
        IssuedJwtToken refreshToken = jwtTokenService.issueRefreshToken(user.getId(), existingToken.getOrganizationId());

        existingToken.setStatus(RefreshTokenStatus.ROTATED);
        existingToken.setRevokedAt(now);
        existingToken.setReplacedByTokenId(refreshToken.tokenId());
        refreshTokenRepository.save(existingToken);
        refreshTokenRepository.save(toRefreshToken(refreshToken, user, existingToken.getOrganizationId(), context));

        List<OrganizationMembership> memberships = membershipRepository.findByUserIdAndStatus(user.getId(), MembershipStatus.ACTIVE);
        auditService.refreshRotated(user.getId(), existingToken.getOrganizationId());
        return toAuthTokenResponse(user, existingToken.getOrganizationId(), accessToken, refreshToken, currentUser, memberships);
    }

    @Transactional
    public LogoutResponse logout(String refreshTokenValue) {
        RefreshTokenClaims claims = jwtTokenService.parseRefreshToken(refreshTokenValue);
        RefreshToken token = refreshTokenRepository.findByTokenId(claims.tokenId())
                .orElseThrow(() -> new AuthenticationFailedException("Refresh token is not recognized"));
        token.setStatus(RefreshTokenStatus.REVOKED);
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
        auditService.logoutSucceeded(token.getUser().getId());
        return new LogoutResponse(true);
    }

    @Transactional
    public void logoutAll(UUID userId) {
        refreshTokenRepository.revokeActiveTokensForUser(userId);
    }

    private Role createOwnerRole(UUID organizationId) {
        List<Permission> permissions = permissionRepository.findByCodeIn(IdentityPermission.allCodes());
        if (permissions.size() != IdentityPermission.values().length) {
            throw new BusinessException("RBAC_PERMISSIONS_NOT_INITIALIZED", "Default RBAC permissions are not initialized");
        }

        Role role = new Role();
        role.setOrganizationId(organizationId);
        role.setCode(OWNER_ROLE_CODE);
        role.setName("Organization Owner");
        role.setSystemRole(false);
        role.setPermissions(Set.copyOf(permissions));
        return roleRepository.save(role);
    }

    private AuthTokenResponse issueTokenResponse(User user, UUID organizationId, List<OrganizationMembership> memberships, AuthRequestContext context) {
        CurrentUser currentUser = currentUserFactory.create(user, organizationId);
        IssuedJwtToken accessToken = jwtTokenService.issueAccessToken(currentUser);
        IssuedJwtToken refreshToken = jwtTokenService.issueRefreshToken(user.getId(), organizationId);
        refreshTokenRepository.save(toRefreshToken(refreshToken, user, organizationId, context));
        return toAuthTokenResponse(user, organizationId, accessToken, refreshToken, currentUser, memberships);
    }

    private RefreshToken toRefreshToken(IssuedJwtToken issuedToken, User user, UUID organizationId, AuthRequestContext context) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenId(issuedToken.tokenId());
        refreshToken.setTokenHash(tokenHashService.sha256(issuedToken.token()));
        refreshToken.setUser(user);
        refreshToken.setOrganizationId(organizationId);
        refreshToken.setExpiresAt(issuedToken.expiresAt());
        refreshToken.setIssuedIp(context.ipAddress());
        refreshToken.setIssuedUserAgent(context.userAgent());
        return refreshToken;
    }

    private AuthTokenResponse toAuthTokenResponse(
            User user,
            UUID organizationId,
            IssuedJwtToken accessToken,
            IssuedJwtToken refreshToken,
            CurrentUser currentUser,
            List<OrganizationMembership> memberships) {
        List<String> permissions = currentUser.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .sorted()
                .toList();
        return new AuthTokenResponse(
                accessToken.token(),
                refreshToken.token(),
                "Bearer",
                accessToken.expiresAt(),
                refreshToken.expiresAt(),
                user.getId(),
                organizationId,
                user.getEmail(),
                permissions,
                memberships.stream().map(this::toMembershipResponse).toList());
    }

    private OrganizationMembershipResponse toMembershipResponse(OrganizationMembership membership) {
        List<String> permissions = membership.getRole().getPermissions().stream()
                .map(Permission::getCode)
                .sorted()
                .toList();
        return new OrganizationMembershipResponse(
                membership.getOrganization().getId(),
                membership.getOrganization().getDisplayName(),
                membership.getRole().getId(),
                membership.getRole().getCode(),
                permissions);
    }

    private UUID resolveOrganizationId(UUID requestedOrganizationId, List<OrganizationMembership> memberships) {
        if (memberships.isEmpty()) {
            throw new UnauthorizedActionException("User does not have access to any active organization");
        }
        if (requestedOrganizationId != null) {
            boolean authorized = memberships.stream()
                    .anyMatch(membership -> membership.getOrganization().getId().equals(requestedOrganizationId));
            if (!authorized) {
                throw new UnauthorizedActionException("User does not have active access to the requested organization");
            }
            return requestedOrganizationId;
        }
        if (memberships.size() > 1) {
            throw new BusinessException("ORGANIZATION_REQUIRED", "Organization must be selected for this user");
        }
        return memberships.stream()
                .min(Comparator.comparing(membership -> membership.getOrganization().getDisplayName()))
                .orElseThrow()
                .getOrganization()
                .getId();
    }

    private AuthenticationFailedException invalidCredentials(String email) {
        auditService.loginFailed(email);
        return new AuthenticationFailedException("Invalid credentials");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
