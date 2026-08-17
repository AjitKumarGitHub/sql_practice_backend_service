package com.sql.gaming.backend.pojo;

import java.util.List;
import java.util.Map;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SqlExecutionResult {

    private boolean success;

    private List<String> columns;

    private List<Map<String, Object>> rows;

    private Long executionTimeMs;

    private String errorMessage;
}
