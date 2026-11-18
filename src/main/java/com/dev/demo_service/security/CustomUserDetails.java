package com.dev.demo_service.security;

import com.dev.demo_service.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Clock clock;

    public CustomUserDetails(User user, Clock clock) {
        this.user = user;
        this.clock = clock;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getPermissions()
                .stream()
                .map(permission ->
                        new SimpleGrantedAuthority(permission.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getLockedUntil() == null ||
                !user.getLockedUntil().isAfter(LocalDateTime.now(clock));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}