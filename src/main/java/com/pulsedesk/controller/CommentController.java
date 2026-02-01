package com.pulsedesk.controller;

import com.pulsedesk.model.Comment;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing comments.
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public CommentController(CommentService commentService,
                             CommentRepository commentRepository) {
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    /**
     * Submits a new comment.
     *
     * @param body Map containing "author" and "text".
     * @return The created Comment object.
     */
    @PostMapping
    public Comment submitComment(@RequestBody Map<String, String> body) {
        return commentService.submitComment(
                body.get("author"),
                body.get("text")
        );
    }

    /**
     * Retrieves all comments.
     *
     * @return List of all comments.
     */
    @GetMapping
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }
}
