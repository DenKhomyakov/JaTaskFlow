package com.example.jataskflow.controller;

import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.dto.response.TaskResponse;
import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
                    @ApiResponse(responseCode = "200", description = "Задача создана"),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = ((User) userDetails).getId();
        taskDto.setAuthorId(currentUserId); // Устанавливаем автора
        Task task = taskService.createTask(taskDto);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    @Operation(
            summary = "Получить все задачи",
            description = "Возвращает список всех задач",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос")
            }
    )
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
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
    @PreAuthorize("hasRole('ADMIN') or @taskServiceImpl.isAuthor(#taskId, #userDetails)")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
