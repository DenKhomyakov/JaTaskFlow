package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.exception.NotFoundException;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.User;
import com.example.jataskflow.repository.CommentRepository;
import com.example.jataskflow.repository.TaskRepository;
import com.example.jataskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Comment addComment(CommentDto commentDto) {
        Task task = taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + commentDto.getTaskId()));

        User author = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + commentDto.getAuthorId()));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
