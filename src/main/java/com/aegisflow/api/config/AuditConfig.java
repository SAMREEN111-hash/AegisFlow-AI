package com.aegisflow.api.config;

import com.aegisflow.api.common.security.SecurityUtils;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    AuditorAware<UUID> auditorProvider() {
        return () -> Optional.ofNullable(SecurityUtils.currentUserId()).or(() -> Optional.of(SecurityUtils.SYSTEM_ACTOR_ID));
    }
}
