package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionMessage {

    private Long submissionId;

    private Long problemId;

    private String studentUserName;
}
