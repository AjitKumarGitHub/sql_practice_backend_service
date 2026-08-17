package com.sql.gaming.backend.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {

    @NotBlank(message = "SQL query is required")
    private String query;
}
