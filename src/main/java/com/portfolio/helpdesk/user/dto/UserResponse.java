package com.portfolio.helpdesk.user.dto;

import com.portfolio.helpdesk.common.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A user account (never includes the password hash)")
public record UserResponse(
        @Schema(example = "7") Long id,
        @Schema(example = "Akhilesh K") String fullName,
        @Schema(example = "akhilesh@example.com") String email,
        @Schema(example = "USER") Role role
) {
}