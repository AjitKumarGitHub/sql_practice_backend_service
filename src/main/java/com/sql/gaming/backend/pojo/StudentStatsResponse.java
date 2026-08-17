package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentStatsResponse {

    private String userName;

    private String name;
    
    private Integer globalRank;

    private Integer batchRank;

    private String batch;

    private Integer totalScore;

    private Integer problemsSolved;

    private Integer easySolved;

    private Integer mediumSolved;

    private Integer hardSolved;

    private Integer totalSubmissions;

    private Integer acceptedSubmissions;
}
