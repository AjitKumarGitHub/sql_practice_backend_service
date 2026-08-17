package com.sql.gaming.backend.pojo;

import java.time.LocalDateTime;
import java.util.List;

import com.sql.gaming.backend.enums.Difficulty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponse {

    private Long id;

    private String title;

    private String slug;

    private String description;

    private Difficulty difficulty;

    private String explanation;

    /**
     * Information about the SQL tables
     * used by this problem.
     */
    private List<SqlTableResponse> tables;

    /**
     * Creator information.
     */
    private String createdBy;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
