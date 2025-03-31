package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.User;
import com.example.jataskflow.repository.CommentRepository;
import com.example.jataskflow.repository.TaskRepository;
import com.example.jataskflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Comment addComment(CommentDto commentDto) {
        Task task = taskRepository.findById(commentDto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User author = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }
}
