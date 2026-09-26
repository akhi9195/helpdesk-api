package com.portfolio.helpdesk.ticket;

import com.portfolio.helpdesk.common.exception.ConcurrentUpdateException;
import com.portfolio.helpdesk.common.exception.InvalidStatusTransitionException;
import com.portfolio.helpdesk.common.exception.OperationNotPermittedException;
import com.portfolio.helpdesk.common.exception.ResourceNotFoundException;
import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.ticket.dto.ChangeStatusRequest;
import com.portfolio.helpdesk.ticket.dto.CreateTicketRequest;
import com.portfolio.helpdesk.ticket.dto.TicketResponse;
import com.portfolio.helpdesk.user.User;
import com.portfolio.helpdesk.user.UserService;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final TicketMapper ticketMapper;
    private final TicketStatusPolicy statusPolicy;
    private final Clock clock;

    /** FR-3, BR-1: the Ticket constructor guarantees OPEN + unassigned. */
    @Transactional
    public TicketResponse create(CreateTicketRequest request, CurrentUser actor) {
        User creator = userService.getUserEntity(actor.id());
        Ticket ticket = new Ticket(
                request.title(),
                request.description(),
                request.priority(),
                request.category(),
                creator);
        ticketRepository.save(ticket);
        log.info("Ticket {} created by user {}", ticket.getId(), actor.id());
        return ticketMapper.toResponse(ticket);
    }

    /** FR-4, BR-2: a USER sees only their own tickets; staff see all. */
    public Page<TicketResponse> list(TicketStatus status, Pageable pageable, CurrentUser actor) {
        Page<Ticket> page;
        if (actor.isStaff()) {
            page = (status == null)
                    ? ticketRepository.findAll(pageable)
                    : ticketRepository.findAllByStatus(status, pageable);
        } else {
            page = (status == null)
                    ? ticketRepository.findAllByCreatedById(actor.id(), pageable)
                    : ticketRepository.findAllByCreatedByIdAndStatus(actor.id(), status, pageable);
        }
        return page.map(ticketMapper::toResponse);
    }

    /** FR-5 */
    public TicketResponse get(Long id, CurrentUser actor) {
        return ticketMapper.toResponse(loadVisibleTicket(id, actor));
    }

    /** FR-6: the whole lifecycle in one transaction, checks in a fixed order. */
    @Transactional
    public TicketResponse changeStatus(Long id, ChangeStatusRequest request, CurrentUser actor) {
        Ticket ticket = loadVisibleTicket(id, actor);                        // 404
        TicketStatus from = ticket.getStatus();
        TicketStatus to = request.status();

        if (!statusPolicy.canTransition(from, to)) {                         // 409
            throw new InvalidStatusTransitionException(from, to);
        }
        if (!statusPolicy.isPermitted(to, actor, ticket.creatorId(), ticket.assigneeId())) {
            throw new OperationNotPermittedException(                        // 403
                    "You are not allowed to move this ticket to " + to);
        }

        apply(ticket, to, actor);

        try {
            // Flush now so a version conflict surfaces here, inside the method,
            // instead of at commit time after the method has already returned.
            ticketRepository.saveAndFlush(ticket);
        } catch (OptimisticLockingFailureException ex) {
            throw new ConcurrentUpdateException("Ticket", id);               // 409, BR-8
        }

        log.info("Ticket {} moved {} -> {} by user {}", id, from, to, actor.id());
        return ticketMapper.toResponse(ticket);
    }

    private void apply(Ticket ticket, TicketStatus to, CurrentUser actor) {
        Instant now = clock.instant();
        switch (to) {
            case IN_PROGRESS -> ticket.startBy(userService.getUserEntity(actor.id()));
            case RESOLVED    -> ticket.resolve(now);
            case CLOSED      -> ticket.close(now);
            case OPEN        -> throw new IllegalStateException("OPEN is never a transition target");
        }
    }

    /** BR-2: another user's ticket is reported as 404, not 403, to hide its existence. */
    private Ticket loadVisibleTicket(Long id, CurrentUser actor) {
        Ticket ticket = ticketRepository.findWithCreatorAndAssigneeById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        if (!actor.isStaff() && !ticket.isCreatedBy(actor.id())) {
            throw new ResourceNotFoundException("Ticket", id);
        }
        return ticket;
    }
}