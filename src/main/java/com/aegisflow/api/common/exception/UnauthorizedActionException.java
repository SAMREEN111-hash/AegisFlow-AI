package com.aegisflow.api.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends AegisFlowException {

    public UnauthorizedActionException(String message) {
        super("UNAUTHORIZED_ACTION", message, HttpStatus.FORBIDDEN);
    }
}
