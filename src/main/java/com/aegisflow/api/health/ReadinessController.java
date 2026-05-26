package com.aegisflow.api.health;

import com.aegisflow.api.common.api.ApiResponse;
import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class ReadinessController {

    @GetMapping("/ready")
    ApiResponse<Map<String, Object>> ready() {
        return ApiResponse.success(Map.of(
                "service", "aegisflow-api",
                "status", "ready",
                "timestamp", Instant.now()));
    }
}
