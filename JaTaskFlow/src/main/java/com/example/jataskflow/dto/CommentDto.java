package com.example.jataskflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Data Transfer Object для комментария")
public class CommentDto {
    @Schema(description = "ID комментария", example = "1")
    private Long id;

    @Schema(description = "Текст комментария", required = true, example = "Это тестовый комментарий", minLength = 1, maxLength = 1000)
    private String text;

    @Schema(description = "ID автора комментария", example = "1")
    private Long authorId;

    @Schema(description = "ID задачи, к которой относится комментарий", example = "1")
    private Long taskId;

    @Schema(description = "Дата и время создания комментария", example = "2025-04-02T12:30:00")
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Long getAuthorId() {
        return this.authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getTaskId() {
        return this.taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
