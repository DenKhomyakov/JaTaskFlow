package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.request.CommentRequest;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.exception.AccessDeniedException;
import com.example.jataskflow.exception.NotFoundException;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.Role;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.User;
import com.example.jataskflow.repository.CommentRepository;
import com.example.jataskflow.repository.TaskRepository;
import com.example.jataskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              TaskRepository taskRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment addComment(CommentDto commentDto) {
        Task task = taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User author = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    @Override
    public Page<CommentResponse> getCommentsByTaskId(Long taskId, Pageable pageable) {
        // Проверка существования задачи
        if (!taskRepository.existsById(taskId)) {
            throw new NotFoundException("Task not found with id: " + taskId);
        }

        // Получаем страницу комментариев
        Page<Comment> commentsPage = commentRepository.findByTaskId(taskId, pageable);

        // Преобразуем в DTO
        return commentsPage.map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setText(comment.getText());
            response.setCreatedAt(comment.getCreatedAt());

            if (comment.getAuthor() != null) {
                response.setAuthorId(comment.getAuthor().getId());
                response.setAuthorName(
                        comment.getAuthor().getFirstname() + " " +
                                comment.getAuthor().getLastname()
                );
            }

            response.setTaskId(comment.getTask().getId());

            return response;
        });
    }

    @Override
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        return convertToResponse(comment);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        // Проверка прав (автор или ADMIN)
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!comment.getAuthor().getId().equals(currentUserId)
                && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("No permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorName(comment.getAuthor().getFirstname() + " " + comment.getAuthor().getLastname());
        }

        if (comment.getTask() != null) {
            response.setTaskId(comment.getTask().getId());
        }

        return response;
    }
}
