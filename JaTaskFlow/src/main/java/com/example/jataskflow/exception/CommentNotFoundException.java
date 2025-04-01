package com.example.jataskflow.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super("Комментарий с ID " + commentId + " не найден");
    }
}