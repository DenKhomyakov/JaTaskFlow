package com.example.jataskflow.dto.request;

import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskRequest {
    @NotBlank(message = "Название задачи обязательно")
    @Size(max = 100, message = "Название не должно превышать 100 символов")
    private String title;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @NotNull(message = "Статус обязателен")
    private Status status;
    @NotNull(message = "Приоритет обязателен")
    private Priority priority;

    private Long authorId;
    private Long executorId;

    public String getTitle() { return this.title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return this.status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return this.priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Long getAuthorId() { return this.authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getExecutorId() { return this.executorId; }
    public void setExecutorId(Long executorId) { this.executorId = executorId; }
}
