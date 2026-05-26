package com.aegisflow.api.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        String requestId,
        Instant timestamp,
        List<FieldErrorDetail> fieldErrors
) {

    public static ErrorResponse of(String code, String message, int status, String path, String requestId) {
        return new ErrorResponse(code, message, status, path, requestId, Instant.now(), List.of());
    }

    public static ErrorResponse validation(String message, int status, String path, String requestId, List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse("VALIDATION_FAILED", message, status, path, requestId, Instant.now(), fieldErrors);
    }
}
