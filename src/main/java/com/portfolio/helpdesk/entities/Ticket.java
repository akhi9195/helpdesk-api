package com.portfolio.helpdesk.entities;

import com.portfolio.helpdesk.enums.ticket.TicketCategory;
import com.portfolio.helpdesk.enums.ticket.TicketPriority;
import com.portfolio.helpdesk.enums.ticket.TicketStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="tickets")
public class Ticket extends BaseEntity {

    @Column(name ="title", nullable = false, length = 150)
    private String  title;

    @Column(name ="description", nullable = false , columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name ="status", nullable = false,length = 20)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name ="category", nullable = false,length = 20)
    private TicketCategory category;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by",nullable = false, updatable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "closed_at")
    private Instant closedAt;


    /** BR-1: a new ticket is always OPEN and unassigned. */
    public Ticket(String title, String description, TicketPriority priority,
                  TicketCategory category, User createdBy) {
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.priority = Objects.requireNonNull(priority, "priority");
        this.category = Objects.requireNonNull(category, "category");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy");
        this.status = TicketStatus.OPEN;
        this.assignedTo = null;
    }



}
