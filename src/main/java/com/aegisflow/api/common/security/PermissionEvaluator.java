package com.aegisflow.api.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permissionEvaluator")
public class PermissionEvaluator {

    public boolean hasPermission(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }
}
