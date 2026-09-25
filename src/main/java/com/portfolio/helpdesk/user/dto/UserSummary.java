package com.portfolio.helpdesk.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Minimal user reference embedded in other resources")
public record UserSummary(
        @Schema(example = "7") Long id,
        @Schema(example = "Akhilesh B") String fullName
) {
}