package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.request.CommentRequest;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.exception.NotFoundException;
import com.example.jataskflow.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(CommentDto commentDto);
    CommentResponse createComment(CommentRequest request);
    List<CommentResponse> getCommentsByTaskId(Long taskId);
    CommentResponse getCommentById(Long id);
    void deleteComment(Long id);
}