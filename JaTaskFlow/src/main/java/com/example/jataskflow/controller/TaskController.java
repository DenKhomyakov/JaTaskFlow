package com.example.jataskflow.controller;

import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.dto.request.TaskRequest;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.dto.response.TaskResponse;
import com.example.jataskflow.exception.GlobalExceptionHandler;
import com.example.jataskflow.model.*;
import com.example.jataskflow.service.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Задачи", description = "API для управления задачами")
public class TaskController {
    private final TaskServiceImpl taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(
            summary = "Создать задачу",
            description = "Доступно только аутентифицированным пользователям",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача создана"),
                    @ApiResponse(responseCode = "400", description = "Неверные данные"),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid TaskRequest request,
            @AuthenticationPrincipal User currentUser) {

        // Создаем DTO для сервиса
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle(request.getTitle());
        taskDto.setDescription(request.getDescription());
        taskDto.setStatus(request.getStatus() != null ? request.getStatus() : Status.WAITING);
        taskDto.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        taskDto.setAuthorId(currentUser.getId()); // Автор - текущий пользователь

        // Устанавливаем исполнителя, если указан
        if (request.getExecutorId() != null) {
            taskDto.setExecutorId(request.getExecutorId());
        }

        // Создаем задачу и возвращаем ответ
        Task createdTask = taskService.createTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToTaskResponse(createdTask));
    }

    @GetMapping
    @Operation(
            summary = "Получить все задачи",
            description = "Возвращает список всех задач",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос")
            }
    )
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> response = tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Фильтрация задач",
            description = "Возвращает задачи с применением фильтров",
            parameters = {
                    @Parameter(name = "status", description = "Фильтр по статусу", example = "IN_PROGRESS"),
                    @Parameter(name = "priority", description = "Фильтр по приоритету", example = "HIGH"),
                    @Parameter(name = "authorId", description = "Фильтр по ID автора", example = "1"),
                    @Parameter(name = "executorId", description = "Фильтр по ID исполнителя", example = "2"),
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class))))
            }
    )
    public List<Task> getFilteredTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long executorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return taskService.getTasksWithFilters(status, priority, authorId, executorId, pageable);
    }

    @GetMapping("/{taskId}")
    @Operation(
            summary = "Получить задачу с комментариями",
            description = "Возвращает задачу и все связанные с ней комментарии",
            parameters = {
                    @Parameter(name = "taskId", description = "ID задачи", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача найдена",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    public ResponseEntity<TaskResponse> getTaskWithComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskWithComments(taskId));
    }

    @DeleteMapping("/{taskId}")
    @Operation(
            summary = "Удаление задачи",
            description = "Доступно только автору задачи или администратору",
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(name = "taskId", description = "ID задачи для удаления", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Задача успешно удалена",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещён (пользователь не является автором или администратором)",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача не найдена",
                            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isAuthor(#taskId, #userDetails)")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/executor")
    @Operation(
            summary = "Назначение исполнителя задачи",
            description = "Доступно только автору задачи. Автор не может быть исполнителем.",
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(name = "taskId", description = "ID задачи", required = true, example = "1"),
                    @Parameter(name = "executorId", description = "ID пользователя-исполнителя", required = true, example = "2")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Исполнитель успешно назначен",
                            content = @Content(schema = @Schema(implementation = TaskResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос (например, попытка назначить автора исполнителем)",
                            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещён (пользователь не является автором задачи)",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Задача или исполнитель не найдены",
                            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponse> setExecutor(
            @PathVariable Long taskId,
            @RequestParam Long executorId,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskResponse response = taskService.setExecutor(taskId, executorId, currentUser);
        return ResponseEntity.ok(response);
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

        // Комментарии преобразуем в CommentResponse
        if (task.getComments() != null) {
            List<CommentResponse> commentResponses = task.getComments().stream()
                    .map(this::convertToCommentResponse)
                    .collect(Collectors.toList());
            response.setComments(commentResponses);
        }

        return response;
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
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

        return response;
    }
}
