package com.example.jataskflow.controller;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment addComment(@RequestBody CommentDto commentDto) {
        return commentService.addComment(commentDto);
    }
}
