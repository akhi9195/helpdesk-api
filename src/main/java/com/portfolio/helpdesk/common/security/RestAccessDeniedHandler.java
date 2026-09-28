package com.portfolio.helpdesk.common.security;

import com.portfolio.helpdesk.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorWriter writer;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        log.debug("Access denied for {} {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());
        writer.write(request, response, ErrorCode.ACCESS_DENIED,
                "You do not have permission to perform this action");
    }
}