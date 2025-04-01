package com.example.jataskflow.controller;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comment> addComment(@Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentService.addComment(commentDto);
        return ResponseEntity.ok(comment);
    }
}
