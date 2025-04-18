package com.example.WITHUS.dto;


import com.example.WITHUS.entity.Team;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class TeamDto {
    private Integer teamId;
    private Integer contIdx;
    private String teamTitle;
    private String teamInfo;
    private int teamLimit;
    private String skill;
    private String region;
    private String target;

    private String userId; // 또는 userNick
    private Timestamp closedAt;
    private Timestamp createdAt;


    // ✅ 기본 생성자 추가 (필수!)
    public TeamDto() {
    }

    public TeamDto(Team team) {
        this.contIdx = team.getContIdx();
        this.teamId = team.getTeamIdx();
        this.teamTitle = team.getTeamTitle();
        this.teamInfo = team.getTeamInfo();
        this.skill = team.getSkill();
        this.region = team.getRegion();
        this.target = team.getTarget();
        this.teamLimit = team.getTeamLimit();
        this.userId = team.getUserId().getUserId(); // 또는 userNick 등
    }
}