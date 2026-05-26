package com.aegisflow.api.reconciliation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RunReconciliationRequest(@NotNull UUID ruleId, @NotBlank String jobName) {
}
