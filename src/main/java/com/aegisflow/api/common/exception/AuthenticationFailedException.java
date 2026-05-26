package com.aegisflow.api.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends AegisFlowException {

    public AuthenticationFailedException(String message) {
        super("AUTHENTICATION_FAILED", message, HttpStatus.UNAUTHORIZED);
    }
}
