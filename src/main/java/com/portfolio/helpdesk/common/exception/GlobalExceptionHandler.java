package com.portfolio.helpdesk.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.core.AuthenticationException;

/**
 * Maps every exception raised in controllers/services to ErrorResponse.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Domain exceptions: 403, 404, 409, 422
    @ExceptionHandler(DomainException.class)
    ResponseEntity<ErrorResponse> handleBusiness(DomainException ex, HttpServletRequest req) {
        log.debug("Business rule violated: {} - {}", ex.getErrorCode(), ex.getMessage());
        return build(ex.getErrorCode(), ex.getMessage(), req, List.of());
    }

    // @Valid on @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex,
                                                       HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return build(ErrorCode.VALIDATION_FAILED, "Request validation failed", req, errors);
    }

    // Constraints on @PathVariable / @RequestParam (built-in MVC method validation)
    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ErrorResponse> handleParamValidation(HandlerMethodValidationException ex,
                                                        HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new ErrorResponse.FieldViolation(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage())))
                .toList();
        return build(ErrorCode.VALIDATION_FAILED, "Request validation failed", req, errors);
    }

    // Unreadable JSON, bad enum in body, wrong type in path/query,
    // missing header or query param (ServletRequestBindingException covers both)
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ServletRequestBindingException.class
    })
    ResponseEntity<ErrorResponse> handleMalformed(Exception ex, HttpServletRequest req) {
        log.debug("Malformed request: {}", ex.getMessage());
        return build(ErrorCode.MALFORMED_REQUEST, "Malformed request", req, List.of());
    }

    // Optimistic lock failure: raised at commit, after the service method returns
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ErrorResponse> handleConcurrent(OptimisticLockingFailureException ex,
                                                   HttpServletRequest req) {
        return build(ErrorCode.CONCURRENT_MODIFICATION,
                "The ticket was modified by someone else; reload and retry", req, List.of());
    }

    // Unknown path
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> handleNoRoute(NoResourceFoundException ex, HttpServletRequest req) {
        return build(ErrorCode.RESOURCE_NOT_FOUND, "No endpoint for this path", req, List.of());
    }

    // Wrong HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleMethod(HttpRequestMethodNotSupportedException ex,
                                               HttpServletRequest req) {
        return build(ErrorCode.METHOD_NOT_ALLOWED, ex.getMessage(), req, List.of());
    }

    // Missing or wrong Content-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex,
                                                  HttpServletRequest req) {
        return build(ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type must be application/json", req, List.of());
    }

    // Anything unexpected: stack trace in logs only
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred", req, List.of());
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code,
                                                String message,
                                                HttpServletRequest req,
                                                List<ErrorResponse.FieldViolation> fieldErrors) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(ErrorResponse.of(code, message, req.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex,
                                                              HttpServletRequest request) {
        String message = (ex instanceof BadCredentialsException)
                ? "Invalid email or password"
                : "Authentication required";
        return build(ErrorCode.UNAUTHORIZED, message, request, null);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDenied(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        return build(ErrorCode.ACCESS_DENIED,
                "You do not have permission to perform this action", request, null);
    }
}