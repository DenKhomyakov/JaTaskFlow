package com.example.jataskflow.service;

import com.example.jataskflow.dto.TaskDto;
import com.example.jataskflow.dto.response.TaskResponse;
import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Task;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TaskService {
    Task createTask(TaskDto taskDto);
    List<Task> getAllTasks();
    List<Task> getTasksWithFilters(Status status, Priority priority, Long authorId, Long executorId, Pageable pageable);
    TaskResponse getTaskWithComments(Long taskId);
    Task updateTaskStatus(Long taskId, Status newStatus, Long currentUserId);
    void deleteTask(Long taskId);
    boolean isAuthor(Long taskId, UserDetails userDetails);
    TaskResponse setExecutor(Long taskId, Long executorId, UserDetails currentUser);
}
