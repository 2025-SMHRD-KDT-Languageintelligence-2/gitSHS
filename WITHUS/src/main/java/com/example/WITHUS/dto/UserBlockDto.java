package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBlockDto {
    private String blockingUserId; // 차단하는 사람
    private String blockedUserId; // 차단당하는 사람
}
