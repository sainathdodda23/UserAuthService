package com.example.UserAuthService.dto;

import com.example.UserAuthService.model.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private Long id;
    private String email;
    private Role role;
}
