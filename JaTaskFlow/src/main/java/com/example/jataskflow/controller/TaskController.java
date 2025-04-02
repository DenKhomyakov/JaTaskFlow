package com.example.jataskflow.controller;

import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.dto.response.TaskResponse;
import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.User;
import com.example.jataskflow.service.TaskServiceImpl;
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
public class TaskController {
    private final TaskServiceImpl taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> createTask(
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = ((User) userDetails).getId();
        taskDto.setAuthorId(currentUserId); // Устанавливаем автора
        Task task = taskService.createTask(taskDto);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/filter")
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
