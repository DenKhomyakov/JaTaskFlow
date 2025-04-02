package com.example.jataskflow.controller;

import com.example.jataskflow.dto.response.TokensResponse;
import com.example.jataskflow.dto.request.LoginRequest;
import com.example.jataskflow.dto.request.RefreshTokenRequest;
import com.example.jataskflow.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "API для входа и обновления токенов")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Вход в систему",
            description = "Возвращает access и refresh токены",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
                    @ApiResponse(responseCode = "401", description = "Неверные логин/пароль")
            }
    )
    public ResponseEntity<TokensResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновление токенов",
            description = "Возвращает новые access и refresh токены",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токены обновлены"),
                    @ApiResponse(responseCode = "401", description = "Недействительный refresh-токен")
            }
    )
    public ResponseEntity<TokensResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshTokens(request));
    }
}
