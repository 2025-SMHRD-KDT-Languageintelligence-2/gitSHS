package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDto {
    private String userId;
    private String userNick;
    private String profileImg;
    private String profileInfo;
}
