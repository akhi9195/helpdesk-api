package com.portfolio.helpdesk.common.config;

import com.portfolio.helpdesk.common.security.CurrentUserArgumentResolver;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // DEV ONLY -
        resolvers.add(new CurrentUserArgumentResolver());
    }
}