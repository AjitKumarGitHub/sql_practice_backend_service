package com.sql.gaming.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.RoleUpdateRequest;
import com.sql.gaming.backend.pojo.StatusUpdateRequest;
import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.UserResponse;
import com.sql.gaming.backend.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * Get All Users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = adminService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .message("Users fetched successfully.")
                        .data(users)
                        .build());
    }

    /**
     * Get User By Username
     */
    @GetMapping("/users/{userName}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable String userName) {

        UserResponse response = adminService.getUser(userName);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User fetched successfully.")
                        .data(response)
                        .build());
    }

    /**
     * Update Role
     */
    @PutMapping("/users/{userName}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateRole(
            @PathVariable String userName,
            @Valid @RequestBody RoleUpdateRequest request) {

        UserResponse response =
                adminService.updateRole(userName, request);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User role updated successfully.")
                        .data(response)
                        .build());
    }

    /**
     * Enable / Disable User
     */
    @PutMapping("/users/{userName}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable String userName,
            @Valid @RequestBody StatusUpdateRequest request) {

        UserResponse response =
                adminService.updateStatus(userName, request);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User status updated successfully.")
                        .data(response)
                        .build());
    }

    /**
     * Delete User
     */
    @DeleteMapping("/users/{userName}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable String userName) {

        adminService.deleteUser(userName);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<String>builder()
                        .success(true)
                        .message("User deleted successfully.")
                        .data(userName)
                        .build());
    }

}