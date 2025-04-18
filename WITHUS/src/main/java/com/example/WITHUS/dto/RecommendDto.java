package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RecommendDto {

    private UserSignupDto user;  // 유저 프로필
    private List<TeamDto> teams;  // 팀 목록
}
