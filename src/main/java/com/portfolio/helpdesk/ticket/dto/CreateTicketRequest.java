package com.portfolio.helpdesk.ticket.dto;

import com.portfolio.helpdesk.ticket.TicketCategory;
import com.portfolio.helpdesk.ticket.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "A new support ticket")
public record CreateTicketRequest(

        @Schema(example = "VPN disconnects every 10 minutes")
        @NotBlank
        @Size(min = 5, max = 150)
        String title,

        @Schema(example = "Since Monday my VPN drops and the laptop becomes slow.")
        @NotBlank
        @Size(min = 10, max = 4000)
        String description,

        @Schema(example = "HIGH")
        @NotNull
        TicketPriority priority,

        @Schema(example = "NETWORK")
        @NotNull
        TicketCategory category
) {
}