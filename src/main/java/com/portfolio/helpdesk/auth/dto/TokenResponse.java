package com.portfolio.helpdesk.auth.dto;

import com.portfolio.helpdesk.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...") String accessToken,
        @Schema(example = "Bearer") String tokenType,
        @Schema(example = "3600") long expiresIn,
        UserResponse user) {
}