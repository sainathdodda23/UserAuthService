package com.example.UserAuthService.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=8)
    private String password;
}
