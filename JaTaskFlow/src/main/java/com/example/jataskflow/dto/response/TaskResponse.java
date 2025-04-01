package com.example.jataskflow.dto.response;

import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.Status;
import java.util.List;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long authorId;
    private Long executorId;
    private List<CommentResponse> comments;

    public Long getId() { return this.id; }

    public void setId(Long id) { this.id = id; }

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

    public List<CommentResponse> getComments() { return this.comments; }

    public void setComments(List<CommentResponse> comments) { this.comments = comments; }
}
