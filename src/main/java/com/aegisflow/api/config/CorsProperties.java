package com.aegisflow.api.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aegisflow.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
