package com.sql.gaming.backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sql.gaming.backend.pojo.LoginRequest;
import com.sql.gaming.backend.pojo.LoginResponse;
import com.sql.gaming.backend.pojo.RegisterRequest;
import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.repository.UserRepository;
import com.sql.gaming.backend.security.JwtService;
import com.sql.gaming.backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

  
    public void register(RegisterRequest request) {

        try {

        	 if (userRepository.existsByUserName(request.getUserName())) {
                 throw new RuntimeException("Username already exists.");
             }
        	 
        	 
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }

            User user = User.builder()
                    .userName(request.getUserName())
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .course(request.getCourse())
                    .batch(request.getBatch())
                    .role(Role.ROLE_STUDENT)
                    .enabled(true)
                    .build();

            userRepository.save(user);

        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage());

        }

    }

    
    public LoginResponse login(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()));

            User user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            String token = jwtService.generateToken(new CustomUserDetails(user));

            return LoginResponse.builder()
                    .token(token)
                    .userName(user.getUserName())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage());

        }

    }
    
    
    public void adminSignup(RegisterRequest request) {

        try {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }

            if (userRepository.existsByUserName(request.getUserName())) {
                throw new RuntimeException("Username already exists.");
            }

            User user = User.builder()
                    .userName(request.getUserName())
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .course(request.getCourse())
                    .batch(request.getBatch())
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(user);

        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage());

        }

    }

    
    public LoginResponse adminLogin(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()));

            User user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            String token = jwtService.generateToken(new CustomUserDetails(user));

            return LoginResponse.builder()
                    .token(token)
                    .userName(user.getUserName())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

        } catch (Exception ex) {

            throw new RuntimeException(ex.getMessage());

        }

    }

}