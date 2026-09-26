package com.portfolio.helpdesk.ticket.dto;

import com.portfolio.helpdesk.ticket.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Target status for a lifecycle transition")
public record ChangeStatusRequest(
        @Schema(description = "Next status: OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED",
                example = "IN_PROGRESS")
        @NotNull
        TicketStatus status
) {
}