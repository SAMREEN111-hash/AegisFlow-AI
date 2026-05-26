package com.aegisflow.api.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AegisFlowException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("RESOURCE_NOT_FOUND", "%s was not found for identifier [%s]".formatted(resourceName, identifier), HttpStatus.NOT_FOUND);
    }
}
