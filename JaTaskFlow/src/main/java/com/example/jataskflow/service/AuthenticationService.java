package com.example.jataskflow.service;

import com.example.jataskflow.dto.response.TokensResponse;
import com.example.jataskflow.dto.request.LoginRequest;
import com.example.jataskflow.dto.request.RefreshTokenRequest;
import com.example.jataskflow.security.JwtTokenUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final JwtTokenUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(
            AuthenticationManager authManager,
            JwtTokenUtils jwtUtils,
            UserDetailsService userDetailsService
    ) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    public TokensResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        return new TokensResponse(
                jwtUtils.generateAccessToken(userDetails),
                jwtUtils.generateRefreshToken(userDetails)
        );
    }

    public TokensResponse refreshTokens(RefreshTokenRequest request) {
        // Временная заглушка (не забыть реализовать позже!)
        throw new UnsupportedOperationException("Refresh token functionality not implemented yet");
    }
}
