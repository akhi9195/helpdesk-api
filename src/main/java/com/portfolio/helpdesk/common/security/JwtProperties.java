package com.portfolio.helpdesk.common.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotNull Duration expiration,
        @NotBlank String issuer) {

    @Override
    public String toString() {
        return "JwtProperties[secret=****, expiration=" + expiration + ", issuer=" + issuer + "]";
    }
}