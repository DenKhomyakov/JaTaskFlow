package com.example.jataskflow.service;

import com.example.jataskflow.model.User;
import com.example.jataskflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {
    private final UserRepository userRepository;

    public UserPrincipalServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // При реализации exception заменить выброс ошибки на NotFoundException!
    }
}
