package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteAccountRequestDto {
    private String password;
    private String confirmPassword;
}