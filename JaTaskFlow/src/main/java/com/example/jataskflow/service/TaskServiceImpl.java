package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.repository.TaskRepository;
import com.example.jataskflow.specification.TaskSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : Status.WAITING);
        task.setPriority(taskDto.getPriority() != null ? taskDto.getPriority() : Priority.MEDIUM);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksWithFilters(Status status, Priority priority,
                                          Long authorId, Long executorId,
                                          Pageable pageable) {

        Specification<Task> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and(TaskSpecifications.hasStatus(status));
        }
        if (priority != null) {
            spec = spec.and(TaskSpecifications.hasPriority(priority));
        }
        if (authorId != null) {
            spec = spec.and(TaskSpecifications.hasAuthor(authorId));
        }
        if (executorId != null) {
            spec = spec.and(TaskSpecifications.hasExecutor(executorId));
        }

        return taskRepository.findAll(spec, pageable).getContent();
    }

    @Override
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

    @Override
    public Task updateTaskStatus(Long taskId, Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(newStatus);
        return taskRepository.save(task);
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
}
