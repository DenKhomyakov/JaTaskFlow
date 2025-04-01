package com.example.jataskflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequest {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    @NotNull(message = "ID задачи обязательно")
    private Long taskId;

    @NotNull(message = "ID автора обязательно")
    private Long authorId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
