package com.portfolio.helpdesk.common.web;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j(topic = "helpdesk.access")
@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

    private static final long SLOW_REQUEST_MS = 1_000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            MDC.put(TraceIdFilter.USER_ID_KEY, auth.getName()); // JWT "sub" = userId (set in Milestone 8)
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (request.getDispatcherType() != DispatcherType.REQUEST) {
            return; // skip error/async re-dispatches, or we'd log twice
        }
        long ms = TraceIdFilter.elapsedMillis(request);
        String handlerName = handler instanceof HandlerMethod hm
                ? hm.getBeanType().getSimpleName() + "." + hm.getMethod().getName()
                : handler.getClass().getSimpleName();

        if (ms >= SLOW_REQUEST_MS) {
            log.warn("{} {} -> {} in {} ms [{}] SLOW", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), ms, handlerName);
        } else {
            log.info("{} {} -> {} in {} ms [{}]", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), ms, handlerName);
        }
        request.setAttribute(TraceIdFilter.LOGGED_ATTR, Boolean.TRUE);
    }
}