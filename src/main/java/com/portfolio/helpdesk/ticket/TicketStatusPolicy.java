package com.portfolio.helpdesk.ticket;

import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.common.security.Role;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Pure rules for the ticket lifecycle: OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED.
 * No database access, so it is trivially unit-testable.
 */
@Component
public class TicketStatusPolicy {

    private static final Map<TicketStatus, TicketStatus> NEXT_STATUS = Map.of(
            TicketStatus.OPEN, TicketStatus.IN_PROGRESS,
            TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED,
            TicketStatus.RESOLVED, TicketStatus.CLOSED);

    /** BR-6: only the single forward step is legal; nothing leaves CLOSED. */
    public boolean canTransition(TicketStatus from, TicketStatus to) {
        return to != null && NEXT_STATUS.get(from) == to;
    }

    /** BR-3, BR-4, BR-5: who may perform the step. ADMIN may do any valid step. */
    public boolean isPermitted(TicketStatus target, CurrentUser actor,
                               Long creatorId, Long assigneeId) {
        if (actor.isAdmin()) {
            return true;
        }
        return switch (target) {
            case IN_PROGRESS -> actor.role() == Role.SUPPORT;      // BR-3
            case RESOLVED    -> actor.id().equals(assigneeId);     // BR-4
            case CLOSED      -> actor.id().equals(creatorId);      // BR-5
            case OPEN        -> false;                             // never a target
        };
    }
}