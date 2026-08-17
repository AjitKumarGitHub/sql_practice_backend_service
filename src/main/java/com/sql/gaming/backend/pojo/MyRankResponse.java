package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyRankResponse {

    private String userName;

    private String name;

    private String batch;

    private Integer totalScore;

    private Integer problemsSolved;

    private Integer globalRank;

    private Integer batchRank;
}