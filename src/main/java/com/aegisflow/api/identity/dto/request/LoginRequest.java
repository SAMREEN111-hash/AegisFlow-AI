package com.aegisflow.api.identity.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        UUID organizationId
) {
}
