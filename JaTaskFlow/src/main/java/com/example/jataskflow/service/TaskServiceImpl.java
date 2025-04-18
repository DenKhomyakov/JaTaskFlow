package com.example.jataskflow.service;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.dto.response.TaskResponse;
import com.example.jataskflow.exception.AccessDeniedException;
import com.example.jataskflow.exception.NotFoundException;
import com.example.jataskflow.exception.TaskNotFoundException;
import com.example.jataskflow.model.*;
import com.example.jataskflow.repository.UserRepository;
import com.example.jataskflow.repository.TaskRepository;
import com.example.jataskflow.specification.TaskSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : Status.WAITING);
        task.setPriority(taskDto.getPriority() != null ? taskDto.getPriority() : Priority.MEDIUM);

        // Проверка исполнителя
        if (taskDto.getExecutorId() != null) {
            User executor = userRepository.findById(taskDto.getExecutorId())
                    .orElseThrow(() -> new NotFoundException("Исполнитель не найден"));
            task.setExecutor(executor);
        }

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
    public TaskResponse getTaskWithComments(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());

        // Добавляем информацию об авторе
        if (task.getAuthor() != null) {
            response.setAuthorId(task.getAuthor().getId());
            response.setAuthorName(task.getAuthor().getFirstname() + " " + task.getAuthor().getLastname());
        }

        // Добавляем информацию об исполнителе
        if (task.getExecutor() != null) {
            response.setExecutorId(task.getExecutor().getId());
            response.setExecutorName(task.getExecutor().getFirstname() + " " + task.getExecutor().getLastname());
        }

        // Добавляем комментарии
        if (task.getComments() != null) {
            List<CommentResponse> commentResponses = task.getComments().stream()
                    .map(comment -> {
                        CommentResponse cr = new CommentResponse();
                        cr.setId(comment.getId());
                        cr.setText(comment.getText());
                        cr.setCreatedAt(comment.getCreatedAt());
                        if (comment.getAuthor() != null) {
                            cr.setAuthorId(comment.getAuthor().getId());
                            cr.setAuthorName(comment.getAuthor().getFirstname() + " " + comment.getAuthor().getLastname());
                        }
                        return cr;
                    })
                    .collect(Collectors.toList());
            response.setComments(commentResponses);
        }

        return response;
    }

    @Override
    public Task updateTaskStatus(Long taskId, Status newStatus, Long currentUserId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isAuthor = task.getAuthor().getId().equals(currentUserId);
        boolean isExecutor = task.getExecutor() != null &&
                task.getExecutor().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isAuthor && !isExecutor && !isAdmin) {
            throw new AccessDeniedException(
                    "Only author, executor or admin can change task status"
            );
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public boolean isAuthor(Long taskId, UserDetails userDetails) {
        User currentUser = (User) userDetails;
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        return task.getAuthor().getId().equals(currentUser.getId());
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(taskId);
    }

    @Override
    @Transactional
    public TaskResponse setExecutor(Long taskId, Long executorId, UserDetails currentUser) {
        // 1. Проверяем существование задачи
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // 2. Проверяем, что у задачи есть автор
        if (task.getAuthor() == null) {
            throw new AccessDeniedException("Задача не имеет автора. Назначение исполнителя невозможно.");
        }

        // 3. Проверяем, что текущий пользователь — автор задачи
        User author = (User) currentUser;
        if (!task.getAuthor().getId().equals(author.getId())) {
            throw new AccessDeniedException("Только автор задачи может назначить исполнителя");
        }

        // 4. Проверяем существование исполнителя
        User executor = userRepository.findById(executorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + executorId + " не найден"));

        // 5. Проверяем, что исполнитель не является автором
        if (executor.getId().equals(author.getId())) {
            throw new IllegalArgumentException("Автор задачи не может быть её исполнителем");
        }

        // 6. Назначаем исполнителя и сохраняем
        task.setExecutor(executor);
        Task updatedTask = taskRepository.save(task);

        return convertToTaskResponse(updatedTask);
    }

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());

        // Информация об авторе
        if (task.getAuthor() != null) {
            response.setAuthorId(task.getAuthor().getId());
            response.setAuthorName(
                    task.getAuthor().getFirstname() + " " +
                            task.getAuthor().getLastname()
            );
        }

        // Информация об исполнителе
        if (task.getExecutor() != null) {
            response.setExecutorId(task.getExecutor().getId());
            response.setExecutorName(
                    task.getExecutor().getFirstname() + " " +
                            task.getExecutor().getLastname()
            );
        }

        // Комментарии
        if (task.getComments() != null) {
            List<CommentResponse> commentResponses = task.getComments().stream()
                    .map(comment -> {
                        CommentResponse cr = new CommentResponse();
                        cr.setId(comment.getId());
                        cr.setText(comment.getText());
                        cr.setCreatedAt(comment.getCreatedAt());
                        if (comment.getAuthor() != null) {
                            cr.setAuthorId(comment.getAuthor().getId());
                            cr.setAuthorName(
                                    comment.getAuthor().getFirstname() + " " +
                                            comment.getAuthor().getLastname()
                            );
                        }
                        return cr;
                    })
                    .collect(Collectors.toList());
            response.setComments(commentResponses);
        }

        return response;
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

    private CommentResponse convertToCommentResponse(Comment comment) {
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
