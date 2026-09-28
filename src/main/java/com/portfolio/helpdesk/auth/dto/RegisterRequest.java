package com.portfolio.helpdesk.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "Akhilesh K")
        @NotBlank @Size(min = 2, max = 100) String fullName,

        @Schema(example = "akhilesh@example.com")
        @NotBlank @Email @Size(max = 150) String email,

        @Schema(example = "Secret123")
        @NotBlank @Size(min = 8, max = 72)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "must contain at least one letter and one digit")
        String password) {
}