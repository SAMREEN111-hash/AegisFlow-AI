package com.aegisflow.api.common.exception;

import com.aegisflow.api.common.api.ApiResponse;
import com.aegisflow.api.common.api.ErrorResponse;
import com.aegisflow.api.common.api.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AegisFlowException.class)
    ResponseEntity<ApiResponse<Void>> handleAegisFlowException(AegisFlowException exception, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
                exception.code(),
                exception.getMessage(),
                exception.status().value(),
                request.getRequestURI(),
                requestId(request));
        return ResponseEntity.status(exception.status()).body(ApiResponse.failure(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorDetail)
                .toList();
        ErrorResponse error = ErrorResponse.validation("Request validation failed", HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(), requestId(request), fieldErrors);
        return ResponseEntity.badRequest().body(ApiResponse.failure(error));
    }

    @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
    ResponseEntity<ApiResponse<Void>> handleConstraintViolation(Exception exception, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of("VALIDATION_FAILED", exception.getMessage(), HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(), requestId(request));
        return ResponseEntity.badRequest().body(ApiResponse.failure(error));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationCredentialsNotFoundException.class})
    ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception exception, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of("ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN.value(),
                request.getRequestURI(), requestId(request));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.failure(error));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleUnhandled(Exception exception, HttpServletRequest request) {
        log.error("Unhandled API exception requestId={} path={}", requestId(request), request.getRequestURI(), exception);
        ErrorResponse error = ErrorResponse.of("INTERNAL_SERVER_ERROR", "Unexpected server error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(), requestId(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.failure(error));
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue());
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute("requestId");
        return requestId == null ? null : requestId.toString();
    }
}
