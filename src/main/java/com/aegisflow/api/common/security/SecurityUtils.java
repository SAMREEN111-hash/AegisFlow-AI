package com.aegisflow.api.common.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    public static final UUID SYSTEM_ACTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private SecurityUtils() {
    }

    public static UUID currentUserId() {
        return currentUser().map(CurrentUser::id).orElse(null);
    }

    public static UUID currentOrganizationId() {
        return currentUser().map(CurrentUser::organizationId).orElse(null);
    }

    public static Optional<CurrentUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return Optional.empty();
        }
        return Optional.of(currentUser);
    }
}
