package com.portfolio.helpdesk.ticket.dto;

import com.portfolio.helpdesk.ticket.TicketCategory;
import com.portfolio.helpdesk.ticket.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to raise a new support ticket")
public record CreateTicketRequest(

        @Schema(description = "Short summary of the problem (5-150 chars)",
                example = "VPN disconnects every 10 minutes")
        @NotBlank
        @Size(min = 5, max = 150)
        String title,

        @Schema(description = "What happened, since when, and what you already tried (10-4000 chars)",
                example = "Since Monday my VPN drops and the laptop becomes slow.")
        @NotBlank
        @Size(min = 10, max = 4000)
        String description,

        @Schema(description = "How urgent the issue is", example = "HIGH")
        @NotNull
        TicketPriority priority,

        @Schema(description = "Area the issue belongs to", example = "NETWORK")
        @NotNull
        TicketCategory category
) {
}