package com.sql.gaming.backend.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	@NotBlank(message = "userName is required")
	private String userName;

	@NotBlank(message = "Password is required")
	private String password;
}
