package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowDto {
    private String userId;
    private String userNick;
    private String profileImg;

    // 필요 시 팔로우 요청용으로도 사용
    private String follower;
    private String followee;
}