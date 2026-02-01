package com.pulsedesk.controller;

import com.pulsedesk.model.Comment;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public Comment submitComment(@RequestBody Map<String, String> body) {
        return commentService.submitComment(
                body.get("author"),
                body.get("text")
        );
    }

    @GetMapping
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }
}
