package com.aegisflow.api.identity.service;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationAuditService {

    public void registrationSucceeded(UUID userId, UUID organizationId) {
        log.info("identity.registration.succeeded userId={} organizationId={}", userId, organizationId);
    }

    public void loginSucceeded(UUID userId, UUID organizationId) {
        log.info("identity.login.succeeded userId={} organizationId={}", userId, organizationId);
    }

    public void loginFailed(String email) {
        log.warn("identity.login.failed email={}", email);
    }

    public void refreshRotated(UUID userId, UUID organizationId) {
        log.info("identity.refresh.rotated userId={} organizationId={}", userId, organizationId);
    }

    public void logoutSucceeded(UUID userId) {
        log.info("identity.logout.succeeded userId={}", userId);
    }
}
