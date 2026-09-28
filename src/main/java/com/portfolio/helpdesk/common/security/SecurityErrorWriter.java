package com.portfolio.helpdesk.common.security;

import com.portfolio.helpdesk.common.exception.ErrorCode;
import com.portfolio.helpdesk.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
class SecurityErrorWriter {

    private final ObjectMapper objectMapper;

    void write(HttpServletRequest request, HttpServletResponse response,
               ErrorCode code, String message) throws IOException {
        response.setStatus(code.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(),
                ErrorResponse.of(code, message, request.getRequestURI(), null));
    }
}