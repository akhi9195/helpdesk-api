package com.portfolio.helpdesk.user.dto;

import com.portfolio.helpdesk.common.security.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "New role for a user")
public record ChangeRoleRequest(
        @Schema(description = "USER or SUPPORT. ADMIN is rejected with 422 INVALID_ROLE_CHANGE.",
                example = "SUPPORT")
        @NotNull
        Role role
) {
}