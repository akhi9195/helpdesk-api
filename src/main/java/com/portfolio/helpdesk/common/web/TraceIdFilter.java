package com.portfolio.helpdesk.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Slf4j(topic = "helpdesk.access")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // before Spring Security's filter chain (order -100)
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String USER_ID_KEY = "userId";

    static final String START_NANOS_ATTR = TraceIdFilter.class.getName() + ".start";
    static final String LOGGED_ATTR = TraceIdFilter.class.getName() + ".logged";

    // Accept a caller's id only if it's safe to write into logs (no newlines, no huge values)
    private static final Pattern VALID_TRACE_ID = Pattern.compile("[A-Za-z0-9-]{8,64}");
    private static final HexFormat HEX = HexFormat.of();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String traceId = resolveTraceId(request.getHeader(TRACE_ID_HEADER));
        MDC.put(TRACE_ID_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId); // set BEFORE the chain so 401s carry it too
        request.setAttribute(START_NANOS_ATTR, System.nanoTime());
        try {
            chain.doFilter(request, response);
        } finally {
            // Requests rejected in the security chain never reach the interceptor: log them here
            if (request.getAttribute(LOGGED_ATTR) == null && !isQuietPath(request.getRequestURI())) {
                log.info("{} {} -> {} in {} ms [no handler]", request.getMethod(),
                        request.getRequestURI(), response.getStatus(), elapsedMillis(request));
            }
            MDC.remove(TRACE_ID_KEY);   // Tomcat reuses threads: never leak MDC into the next request
            MDC.remove(USER_ID_KEY);
        }
    }

    public static String currentTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    static long elapsedMillis(HttpServletRequest request) {
        Object start = request.getAttribute(START_NANOS_ATTR);
        return start instanceof Long s ? (System.nanoTime() - s) / 1_000_000 : -1;
    }

    private static String resolveTraceId(String incoming) {
        if (incoming != null && VALID_TRACE_ID.matcher(incoming).matches()) {
            return incoming;
        }
        return HEX.toHexDigits(ThreadLocalRandom.current().nextLong()); // 16 hex chars, e.g. 4bf92f3577b34da6
    }

    private static boolean isQuietPath(String uri) {
        return uri.startsWith("/actuator") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs");
    }
}