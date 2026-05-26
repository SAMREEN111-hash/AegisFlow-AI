package com.aegisflow.api.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@ConfigurationPropertiesScan(basePackages = "com.aegisflow.api")
public class ApplicationConfig {
}
