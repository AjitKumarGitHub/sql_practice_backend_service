package com.sql.gaming.backend.pojo;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class LoginResponse {

    private String token;

    private String userName;
    
    private String email;

    private String role;

    private String name;

}