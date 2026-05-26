package com.aegisflow.api.identity.service;

import com.aegisflow.api.common.exception.UnauthorizedActionException;
import com.aegisflow.api.common.security.CurrentUser;
import com.aegisflow.api.identity.domain.MembershipStatus;
import com.aegisflow.api.identity.domain.OrganizationMembership;
import com.aegisflow.api.identity.domain.User;
import com.aegisflow.api.identity.repository.OrganizationMembershipRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserFactory {

    private final OrganizationMembershipRepository membershipRepository;

    public CurrentUser create(User user, UUID organizationId) {
        OrganizationMembership membership = membershipRepository
                .findByUserIdAndOrganizationIdAndStatus(user.getId(), organizationId, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new UnauthorizedActionException("User does not have active access to the organization"));

        List<GrantedAuthority> authorities = membership.getRole().getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                .map(GrantedAuthority.class::cast)
                .toList();

        return new CurrentUser(user.getId(), membership.getOrganization().getId(), user.getEmail(), authorities);
    }
}
