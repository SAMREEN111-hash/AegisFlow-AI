package com.aegisflow.api.common.api;

public record FieldErrorDetail(String field, String message, Object rejectedValue) {
}
