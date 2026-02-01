package com.pulsedesk.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a support ticket created from a user comment.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    @Column(length = 2000)
    private String summary;

    /**
     * The original comment that triggered this ticket's creation.
     */
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "original_comment_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_ticket_original_comment")
    )
    private Comment originalComment;
}
