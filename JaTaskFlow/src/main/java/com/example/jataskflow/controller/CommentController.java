package com.example.jataskflow.controller;

import com.example.jataskflow.dto.CommentDto;
import com.example.jataskflow.dto.request.CommentRequest;
import com.example.jataskflow.dto.response.CommentResponse;
import com.example.jataskflow.model.Comment;
import com.example.jataskflow.model.User;
import com.example.jataskflow.service.CommentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Комментарии", description = "API для управления комментариями")
public class CommentController {
    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @Operation(
            summary = "Добавить комментарий",
            description = "Доступно только аутентифицированным пользователям",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарий добавлен"),
                    @ApiResponse(responseCode = "400", description = "Невалидные данные"),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(@RequestBody @Valid CommentRequest request) {
        Comment comment = commentService.addComment(request);
        return ResponseEntity.ok(convertToCommentResponse(comment));
    }

    @GetMapping
    @Operation(
            summary = "Получить комментарии к задаче",
            description = "Возвращает список комментариев с пагинацией",
            parameters = {
                    @Parameter(name = "taskId", description = "ID задачи", required = true, example = "1"),
                    @Parameter(name = "page", description = "Номер страницы", example = "0"),
                    @Parameter(name = "size", description = "Размер страницы", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный запрос",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            }
    )
    public ResponseEntity<Page<CommentResponse>> getComments(
            @RequestParam Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId, pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить комментарий",
            description = "Доступно только автору комментария или администратору",
            security = @SecurityRequirement(name = "JWT"),
            parameters = {
                    @Parameter(name = "id", description = "ID комментария", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий удален"),
                    @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((User) userDetails).getId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }

    private CommentResponse convertToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt());

        // Добавляем только нужные данные, без циклических ссылок
        if (comment.getAuthor() != null) {
            response.setAuthorId(comment.getAuthor().getId());
            response.setAuthorName(comment.getAuthor().getFirstname() + " " + comment.getAuthor().getLastname());
        }

        if (comment.getTask() != null) {
            response.setTaskId(comment.getTask().getId());
        }

        return response;
    }
}
