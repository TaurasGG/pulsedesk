package com.pulsedesk.repository;

import com.pulsedesk.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing {@link com.pulsedesk.model.Comment} entities.
 * Provides CRUD operations backed by Spring Data JPA.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
