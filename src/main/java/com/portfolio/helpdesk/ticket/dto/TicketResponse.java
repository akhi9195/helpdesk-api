package com.portfolio.helpdesk.ticket.dto;

import com.portfolio.helpdesk.ticket.TicketCategory;
import com.portfolio.helpdesk.ticket.TicketPriority;
import com.portfolio.helpdesk.ticket.TicketStatus;
import com.portfolio.helpdesk.user.dto.UserSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A support ticket with creator and assignee summaries")
public record TicketResponse(
        @Schema(example = "42") Long id,
        @Schema(example = "VPN disconnects every 10 minutes") String title,
        @Schema(example = "Since Monday my VPN drops and the laptop becomes slow.") String description,
        @Schema(example = "OPEN") TicketStatus status,
        @Schema(example = "HIGH") TicketPriority priority,
        @Schema(example = "NETWORK") TicketCategory category,
        UserSummary createdBy,
        @Schema(description = "Null until a SUPPORT user starts the ticket") UserSummary assignedTo,
        @Schema(example = "2026-09-23T10:15:30Z") Instant createdAt,
        @Schema(example = "2026-09-23T10:15:30Z") Instant updatedAt,
        @Schema(description = "Set automatically when the ticket becomes RESOLVED") Instant resolvedAt,
        @Schema(description = "Set automatically when the ticket becomes CLOSED") Instant closedAt
) {
}