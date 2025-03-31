package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public TaskDto getTaskWithComments(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setPriority(task.getPriority());

        if (task.getAuthor() != null) {
            taskDto.setAuthorId(task.getAuthor().getId());
        }

        if (task.getExecutor() != null) {
            taskDto.setExecutorId(task.getExecutor().getId());
        }

        if (task.getComments() != null) {
            List<CommentDto> commentDtos = task.getComments().stream()
                    .map(this::convertToCommentDto)
                    .collect(Collectors.toList());
            taskDto.setComments(commentDtos);
        }

        return taskDto;
    }

    private CommentDto convertToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());

        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
        }

        if (comment.getTask() != null) {
            dto.setTaskId(comment.getTask().getId());
        }

        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    public Task updateTaskStatus(Long taskId, Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }
}
