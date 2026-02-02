package com.pulsedesk.repository;

import com.pulsedesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing {@link com.pulsedesk.model.Ticket} entities.
 * Provides CRUD operations backed by Spring Data JPA.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
