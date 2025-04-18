package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserSignupDto {
    // 회원가입
    private String userId;
    private String userPassword;
    private String userPasswordCheck;
    private String userName;
    private String userEmail;
    private LocalDate userBirthdate;

    // 프로필 생성
    private String userNick;
    private String userSkill;
    private String userRegion;
    private String userTarget;
    private String profileImg;

    public UserSignupDto() {
    }

    public UserSignupDto(String userSkill, String userRegion, String userTarget) {
        this.userSkill = userSkill;
        this.userRegion = userRegion;
        this.userTarget = userTarget;
    }
}
