package com.pulsedesk.controller;

import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.TicketRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for retrieving tickets.
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Retrieves all tickets.
     *
     * @return List of all tickets.
     */
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Retrieves a specific ticket by ID.
     *
     * @param id The ID of the ticket.
     * @return The Ticket object.
     */
    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id).orElseThrow();
    }
}
