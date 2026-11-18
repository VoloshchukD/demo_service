package com.dev.demo_service.security;

import com.dev.demo_service.service.BruteForceProtectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    private final BruteForceProtectionService protectionService;

    public CustomAuthenticationFailureHandler(
            BruteForceProtectionService protectionService
    ) {
        this.protectionService = protectionService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        String email = request.getParameter("username");

        if (!(exception instanceof LockedException)
                && email != null
                && !email.isBlank()) {

            protectionService.handleFailedAttempt(email);
        }

        response.sendRedirect("/login?error");
    }

}