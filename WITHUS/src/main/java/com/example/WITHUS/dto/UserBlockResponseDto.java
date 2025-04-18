package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBlockResponseDto {
    private String blockedUserId; // 차단당하는 사람
    private String blockedAt; // 차단 당한 시점
}
