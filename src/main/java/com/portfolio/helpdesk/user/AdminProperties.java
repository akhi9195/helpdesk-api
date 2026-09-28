package com.portfolio.helpdesk.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(String email, String password, String fullName) {

    @Override
    public String toString() {
        return "AdminProperties[email=" + email + ", password=****, fullName=" + fullName + "]";
    }
}