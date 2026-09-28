package com.portfolio.helpdesk.common.config;

import com.portfolio.helpdesk.common.security.CurrentUserArgumentResolver;

import java.util.List;

import com.portfolio.helpdesk.common.web.RequestTimingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final RequestTimingInterceptor requestTimingInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(new CurrentUserArgumentResolver());
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTimingInterceptor).addPathPatterns("/api/**");
    }
}