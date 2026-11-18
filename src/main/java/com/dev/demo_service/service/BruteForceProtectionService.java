package com.dev.demo_service.service;

import com.dev.demo_service.entity.User;
import com.dev.demo_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 5;

    private final UserRepository userRepository;
    private final Clock clock;

    public BruteForceProtectionService(
            UserRepository userRepository,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public void handleFailedAttempt(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmailForUpdate(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        LocalDateTime now = LocalDateTime.now(clock);

        if (user.getLockedUntil() != null) {

            if (user.getLockedUntil().isAfter(now)) {
                return;
            }

            user.resetFailedAttempts();
        }

        user.setFailedAttempts(user.getFailedAttempts() + 1);

        if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
            user.setLockedUntil(
                    now.plusMinutes(LOCK_DURATION_MINUTES)
            );
        }
    }

    @Transactional
    public void resetFailedAttempts(String email) {

        userRepository.findByEmailForUpdate(email)
                .ifPresent(User::resetFailedAttempts);
    }

}