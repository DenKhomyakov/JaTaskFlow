package com.example.jataskflow.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Schema(description = "Сущность комментария")
@Entity
@Table(name = "comments")
public class Comment {
    @Schema(description = "ID комментария", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Текст комментария", example = "Это тестовый комментарий")
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return this.text; }
    public void setText(String text) { this.text = text; }

    public User getAuthor() { return this.author; }
    public void setAuthor(User author) { this.author = author; }

    public Task getTask() { return this.task; }
    public void setTask(Task task) { this.task = task; }

    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
