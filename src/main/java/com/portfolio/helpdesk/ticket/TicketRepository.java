package com.portfolio.helpdesk.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TicketRepository extends JpaRepository<Ticket,Long> {

    @Override
    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Page<Ticket> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Page<Ticket> findAllByCreatedById(Long userId,Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Page<Ticket> findAllByCreatedByIdAndStatus(Long userId, TicketStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "assignedTo"})
    Optional<Ticket> findWithCreatorAndAssigneeById(Long id);
}
