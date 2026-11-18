package com.dev.demo_service.controller;

import com.dev.demo_service.entity.BlockedUserResponse;
import com.dev.demo_service.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class InfoController {

    private final UserRepository userRepository;

    public InfoController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('VIEW_INFO')")
    @GetMapping("/info")
    public Map<String, String> getInfo() {
        return Map.of(
                "application", "MVC application",
                "version", "1.0",
                "status", "running"
        );
    }

    @GetMapping("/about")
    public Map<String, String> about() {
        return Map.of(
                "name", "Demo Service",
                "description", "Spring Boot MVC application"
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('VIEW_ADMIN')")
    public Map<String, String> admin() {
        return Map.of(
                "message", "Welcome to the admin area"
        );
    }

    @GetMapping("/blocked-users")
    @PreAuthorize("hasAuthority('VIEW_ADMIN')")
    public List<BlockedUserResponse> getBlockedUsers() {

        return userRepository
                .findByLockedUntilIsNotNull()
                .stream()
                .map(user -> new BlockedUserResponse(
                        user.getEmail(),
                        user.getLockedUntil()
                ))
                .toList();
    }

}