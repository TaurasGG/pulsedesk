package com.pulsedesk.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user comment submitted to the system.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;

    @Column(length = 2000)
    private String text;

    private boolean ticketCreated;
}
