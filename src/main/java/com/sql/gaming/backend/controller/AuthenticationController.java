package com.sql.gaming.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.LoginRequest;
import com.sql.gaming.backend.pojo.LoginResponse;
import com.sql.gaming.backend.pojo.RegisterRequest;
import com.sql.gaming.backend.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        try {

            authenticationService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User Registered Successfully");

        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        try {

            LoginResponse response = authenticationService.login(request);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());

        }

    }
  
    @PostMapping("/admin/signup")
    public ResponseEntity<?> adminSignup(@Valid @RequestBody RegisterRequest request) {

        try {

            authenticationService.adminSignup(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Admin Registered Successfully");

        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());

        }

    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request) {

        try {

            LoginResponse response = authenticationService.adminLogin(request);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());

        }

    }
}