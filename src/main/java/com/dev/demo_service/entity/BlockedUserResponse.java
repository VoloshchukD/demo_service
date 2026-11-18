package com.dev.demo_service.entity;

import java.time.LocalDateTime;

public record BlockedUserResponse(
        String email,
        LocalDateTime lockedUntil
) {
}