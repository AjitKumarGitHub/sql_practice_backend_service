package com.sql.gaming.backend.pojo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 6, max = 40)
    private String userName;

    @NotBlank
    private String name;

    @Email
    private String email;

    @Size(min = 6)
    private String password;

    private String course;

    private String batch;

}

 