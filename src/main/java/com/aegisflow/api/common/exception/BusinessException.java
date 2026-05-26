package com.aegisflow.api.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends AegisFlowException {

    public BusinessException(String code, String message) {
        super(code, message, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
