package com.portfolio.helpdesk.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.demo")
public record DemoProperties(boolean enabled, Account support, Account user) {
    public record Account(String email, String password, String fullName) {}
}