package com.portfolio.helpdesk.common.web;

import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.common.security.Role;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * DEV ONLY - delete in Milestone 8.
 * Builds CurrentUser from X-User-Id and X-User-Role headers until JWT exists.
 */
public class DevCurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    static final String USER_ID_HEADER = "X-User-Id";
    static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory)
            throws ServletRequestBindingException {

        String id = webRequest.getHeader(USER_ID_HEADER);
        String role = webRequest.getHeader(USER_ROLE_HEADER);

        if (id == null || id.isBlank() || role == null || role.isBlank()) {
            throw new ServletRequestBindingException(
                    USER_ID_HEADER + " and " + USER_ROLE_HEADER + " headers are required");
        }
        try {
            return new CurrentUser(Long.valueOf(id.trim()), Role.valueOf(role.trim()));
        } catch (IllegalArgumentException e) {
            throw new ServletRequestBindingException(
                    "Invalid " + USER_ID_HEADER + " or " + USER_ROLE_HEADER);
        }
    }
}