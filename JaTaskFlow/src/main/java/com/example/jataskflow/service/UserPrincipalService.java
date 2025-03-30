package com.example.jataskflow.service;

import com.example.jataskflow.model.User;

import java.security.Principal;

public interface UserPrincipalService {
    User getUserFromPrincipal(Principal principal);
}
