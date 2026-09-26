package com.portfolio.helpdesk.ticket;

import com.portfolio.helpdesk.common.exception.DomainException;
import com.portfolio.helpdesk.common.exception.ErrorCode;
import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.common.web.PageResponse;
import com.portfolio.helpdesk.ticket.dto.ChangeStatusRequest;
import com.portfolio.helpdesk.ticket.dto.CreateTicketRequest;
import com.portfolio.helpdesk.ticket.dto.TicketResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Create, list, view and progress support tickets")
class TicketController {

    private static final Set<String> SORTABLE = Set.of("createdAt", "priority");

    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a ticket",
            description = "Caller: USER. The ticket starts as OPEN and unassigned. "
                    + "Returns 201 with a Location header pointing to the new ticket.")
    ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request,
                                          CurrentUser currentUser) {
        TicketResponse created = ticketService.create(request, currentUser);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List tickets",
            description = "USER sees only own tickets; SUPPORT and ADMIN see all. "
                    + "Sort by createdAt or priority, e.g. sort=createdAt,desc. Max page size 100.")
    PageResponse<TicketResponse> list(
            @Parameter(description = "Filter by status; omit to get every status", example = "OPEN")
            @RequestParam(required = false) TicketStatus status,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            CurrentUser currentUser) {
        requireSortable(pageable.getSort());
        return PageResponse.from(ticketService.list(status, pageable, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "View a ticket",
            description = "Caller: the ticket's creator, SUPPORT or ADMIN. "
                    + "A USER opening someone else's ticket gets 404.")
    TicketResponse get(@Parameter(description = "Ticket id", example = "42")
                       @PathVariable @Positive Long id,
                       CurrentUser currentUser) {
        return ticketService.get(id, currentUser);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change ticket status",
            description = "Forward-only lifecycle. OPEN -> IN_PROGRESS: SUPPORT or ADMIN (caller becomes assignee). "
                    + "IN_PROGRESS -> RESOLVED: assignee or ADMIN. RESOLVED -> CLOSED: creator or ADMIN. "
                    + "Any other move returns 409 INVALID_STATUS_TRANSITION.")
    TicketResponse changeStatus(
            @Parameter(description = "Ticket id", example = "42")
            @PathVariable @Positive Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChangeStatusRequest.class),
                            examples = {
                                    @ExampleObject(name = "Start work",
                                            summary = "SUPPORT starts an OPEN ticket",
                                            value = "{ \"status\": \"IN_PROGRESS\" }"),
                                    @ExampleObject(name = "Resolve",
                                            summary = "Assignee resolves an IN_PROGRESS ticket",
                                            value = "{ \"status\": \"RESOLVED\" }"),
                                    @ExampleObject(name = "Close",
                                            summary = "Creator closes a RESOLVED ticket",
                                            value = "{ \"status\": \"CLOSED\" }")
                            }))
            @Valid @RequestBody ChangeStatusRequest request,
            CurrentUser currentUser) {
        return ticketService.changeStatus(id, request, currentUser);
    }

    private void requireSortable(Sort sort) {
        for (Sort.Order order : sort) {
            if (!SORTABLE.contains(order.getProperty())) {
                throw new DomainException(ErrorCode.VALIDATION_FAILED,
                        "Sorting is allowed only by " + SORTABLE) {
                    @Override
                    public ErrorCode getErrorCode() {
                        return super.getErrorCode();
                    }
                };
            }
        }
    }
}