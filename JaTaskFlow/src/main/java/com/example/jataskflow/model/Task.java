package com.example.jataskflow.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;

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

    public User getAuthor() { return this.author; }
    public void setAuthor(User author) { this.author = author; }

    public User getExecutor() { return this.executor; }
    public void setExecutor(User executor) { this.executor = executor; }

    public List<Comment> getComments() { return this.comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
