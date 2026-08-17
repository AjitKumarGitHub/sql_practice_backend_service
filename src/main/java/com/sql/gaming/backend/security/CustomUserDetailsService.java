package com.sql.gaming.backend.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUserName(userName)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}
