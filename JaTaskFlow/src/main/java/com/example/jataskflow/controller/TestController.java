package com.example.jataskflow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String hello() {
        return "Task Manager Backend is working!";
    }

    @GetMapping("/check-auth")
    public String checkAuth(@AuthenticationPrincipal UserDetails user) {
        return "Authenticated as: " + user.getUsername() +
                "\nRoles: " + user.getAuthorities();
    }
}
