package com.portfolio.helpdesk.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface TicketRepository extends JpaRepository<Ticket,Long> {
    Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);
    Page<Ticket> findAllByCreatedById(Long userId,Pageable pageable);
    Page<Ticket> findAllByCreatedByIdAndStatus(Long userId, TicketStatus status, Pageable pageable);
}
