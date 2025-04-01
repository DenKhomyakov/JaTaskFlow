package com.example.jataskflow.controller;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.User;
import com.example.jataskflow.service.CommentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @RequestParam Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((User) userDetails).getId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }
}
