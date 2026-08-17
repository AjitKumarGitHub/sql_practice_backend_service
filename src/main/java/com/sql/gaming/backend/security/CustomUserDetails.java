package com.sql.gaming.backend.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sql.gaming.backend.pojo.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(
                new SimpleGrantedAuthority(
                        user.getRole().name()
                )
        );
    }

    @Override
    public String getUsername() {

        return user.getUserName();

    }

    @Override
    public String getPassword() {

        return user.getPassword();

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

        return Boolean.TRUE.equals(user.getEnabled());

    }
}