package com.sql.gaming.backend.pojo;

import java.time.LocalDateTime;

import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.enums.SubmissionStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {

    private Long submissionId;

    private Long problemId;

    private String problemTitle;

    private SubmissionStatus status;

    private Integer score;

    private Integer attemptNumber;

    private Long executionTimeMs;

    private String message;
    
    private String submittedQuery;
    
    private Difficulty difficulty;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
