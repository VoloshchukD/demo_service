package com.dev.demo_service.security;

import com.dev.demo_service.service.BruteForceProtectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final BruteForceProtectionService protectionService;

    public CustomAuthenticationSuccessHandler(
            BruteForceProtectionService protectionService
    ) {
        this.protectionService = protectionService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        protectionService.resetFailedAttempts(
                authentication.getName()
        );

        response.sendRedirect("/home");
    }
}
