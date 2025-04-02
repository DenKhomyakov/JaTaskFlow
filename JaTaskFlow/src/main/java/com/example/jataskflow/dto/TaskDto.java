package com.example.jataskflow.dto;

import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Data Transfer Object для задачи")
public class TaskDto {
    @Schema(description = "ID задачи", example = "1")
    private Long id;

    @Schema(description = "Название задачи", required = true, example = "Разработать API", minLength = 3, maxLength = 100)
    private String title;

    @Schema(description = "Подробное описание задачи", example = "Создать REST API для системы", maxLength = 500)
    private String description;

    @Schema(description = "Текущий статус задачи", implementation = Status.class, example = "WAITING")
    private Status status;

    @Schema(description = "Приоритет задачи", implementation = Priority.class, example = "HIGH")
    private Priority priority;

    @Schema(description = "ID автора задачи", example = "1")
    private Long authorId;

    @Schema(description = "ID исполнителя задачи", example = "2")
    private Long executorId;

    @Schema(description = "Список комментариев к задаче")
    private List<CommentDto> comments;

    public Long getId() { return this.id; }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() { return this.description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return this.status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return this.priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getAuthorId() {
        return this.authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getExecutorId() { return this.executorId; }
    public void setExecutorId(Long executorId) { this.executorId = executorId; }

    public List<CommentDto> getComments() {
        return this.comments;
    }
    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }
}
