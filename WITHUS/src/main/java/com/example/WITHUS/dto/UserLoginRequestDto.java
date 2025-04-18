package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginRequestDto {
    private String userId;
    private String userPassword;
}
