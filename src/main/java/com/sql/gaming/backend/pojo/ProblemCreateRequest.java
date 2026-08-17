package com.sql.gaming.backend.pojo;

import java.util.List;

import com.sql.gaming.backend.enums.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemCreateRequest {

    @NotBlank(message = "Problem title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Problem description is required")
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotBlank(message = "Expected query is required")
    private String expectedQuery;

    private String explanation;

    /**
     * IDs of the predefined SQL tables.
     *
     * Example:
     * [1, 2, 5]
     */
    @NotEmpty(message = "At least one SQL table is required")
    private List<Long> tableIds;

}