package com.marjane.ems.Services;

import com.marjane.ems.Entities.User;
import com.marjane.ems.Entities.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation for Spring Security.
 * Maps User entity to Spring Security's UserDetails interface.
 */
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert Role enum to GrantedAuthority for Spring Security
        return Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus().isActive();
    }

    /**
     * Get the underlying User entity.
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the user's ID.
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * Get the user's EID.
     */
    public String getEid() {
        return user.getEid();
    }

    /**
     * Get the user's role.
     */
    public Role getRole() {
        return user.getRole();
    }
}
