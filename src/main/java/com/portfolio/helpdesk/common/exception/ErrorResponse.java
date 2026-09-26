package com.portfolio.helpdesk.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<FieldViolation> fieldErrors) {

    public record FieldViolation(String field, String message) {}

    public static ErrorResponse of(ErrorCode code,
                                   String message,
                                   String path,
                                   List<FieldViolation> fieldErrors) {
        return new ErrorResponse(
                Instant.now(),
                code.getHttpStatus().value(),
                code.name(),
                message,
                path,
                MDC.get("traceId"),
                fieldErrors);
    }
}