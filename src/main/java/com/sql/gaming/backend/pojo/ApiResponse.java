package com.sql.gaming.backend.pojo;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

    @Builder.Default 
    private LocalDateTime timestamp = LocalDateTime.now();
}