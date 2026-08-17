package com.sql.gaming.backend.pojo;

import com.sql.gaming.backend.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

}
