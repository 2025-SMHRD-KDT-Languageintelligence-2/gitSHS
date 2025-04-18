package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordRequestDto {
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;
}