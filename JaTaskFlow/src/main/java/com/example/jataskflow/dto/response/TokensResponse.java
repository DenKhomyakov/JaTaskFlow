package com.example.jataskflow.dto.response;

public record TokensResponse(
        String accessToken,
        String refreshToken
) {}
