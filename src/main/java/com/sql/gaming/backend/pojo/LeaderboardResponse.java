package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponse {

    private Integer rank;

    private String userName;

    private String name;

    private String batch;

    private Integer totalScore;

    private Integer problemsSolved;

    private Integer easySolved;

    private Integer mediumSolved;

    private Integer hardSolved;
}