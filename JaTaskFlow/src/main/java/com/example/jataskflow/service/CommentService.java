package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.request.CommentRequest;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.exception.NotFoundException;
import com.example.jataskflow.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Comment addComment(CommentDto commentDto);
    CommentResponse createComment(CommentRequest request);
    CommentResponse getCommentById(Long id);
    void deleteComment(Long id);
    Page<CommentResponse> getCommentsByTaskId(Long taskId, Pageable pageable);
}