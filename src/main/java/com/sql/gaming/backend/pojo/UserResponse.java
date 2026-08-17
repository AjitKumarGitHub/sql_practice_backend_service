package com.sql.gaming.backend.pojo;

import java.time.LocalDateTime;

import com.sql.gaming.backend.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String userName;

    private String name;

    private String email;

    private String course;

    private String batch;

    private Role role;

    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
