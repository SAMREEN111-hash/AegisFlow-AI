package com.aegisflow.api.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AegisFlowException {

    public ValidationException(String message) {
        super("VALIDATION_FAILED", message, HttpStatus.BAD_REQUEST);
    }
}
