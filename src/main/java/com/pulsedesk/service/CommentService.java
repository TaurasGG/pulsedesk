package com.pulsedesk.service;

import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

/**
 * Service class for managing comments and orchestrating the AI analysis flow.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final HuggingFaceService aiService;

    public CommentService(CommentRepository commentRepository,
                          TicketRepository ticketRepository,
                          HuggingFaceService aiService) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.aiService = aiService;
    }

    /**
     * Submits a new comment, saves it, triggers AI analysis, and creates a ticket if necessary.
     *
     * @param author The name of the comment author.
     * @param text   The content of the comment.
     * @return The saved Comment entity (with updated ticketCreated status).
     */
    public Comment submitComment(String author, String text) {

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(text);
        comment.setTicketCreated(false);

        comment = commentRepository.save(comment);

        AiTicketResponse aiResponse = aiService.analyzeComment(text);

        if (aiResponse.isTicket()) {
            Ticket ticket = new Ticket();
            ticket.setTitle(aiResponse.getTitle());
            ticket.setCategory(aiResponse.getCategory());
            ticket.setPriority(aiResponse.getPriority());
            ticket.setSummary(aiResponse.getSummary());
            ticket.setOriginalComment(comment);

            ticketRepository.save(ticket);
            comment.setTicketCreated(true);
        }

        return commentRepository.save(comment);
    }
}
