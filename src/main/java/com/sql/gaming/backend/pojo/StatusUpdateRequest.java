package com.sql.gaming.backend.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private Boolean enabled;

}