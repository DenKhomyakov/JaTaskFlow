package com.example.jataskflow.specification;

import com.example.jataskflow.model.Task;
import com.example.jataskflow.model.Status;
import com.example.jataskflow.model.Priority;
import com.example.jataskflow.model.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class TaskSpecifications {

    public static Specification<Task> hasStatus(Status status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAuthor(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) return null;
            Join<Task, User> authorJoin = root.join("author");
            return cb.equal(authorJoin.get("id"), authorId);
        };
    }

    public static Specification<Task> hasExecutor(Long executorId) {
        return (root, query, cb) -> {
            if (executorId == null) return null;
            Join<Task, User> executorJoin = root.join("executor");
            return cb.equal(executorJoin.get("id"), executorId);
        };
    }
}