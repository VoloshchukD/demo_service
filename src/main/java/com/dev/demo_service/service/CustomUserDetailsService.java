package com.dev.demo_service.service;

import com.dev.demo_service.security.CustomUserDetails;
import com.dev.demo_service.entity.User;
import com.dev.demo_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;
    private final Clock clock;

    public CustomUserDetailsService(
            UserRepository userRepository,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        return new CustomUserDetails(user, clock);
    }

}