package com.example.myproject.dto;

import com.example.myproject.Model.User.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    private String username;
    private String password;
    private Role role;
}