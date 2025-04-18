package com.example.WITHUS.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Data

public class ChatMessageDto {
    private Integer croomIdx;
    private String senderId;
    private String sender;   // 유저 닉네임
    private String content;
    private String createdAt;

    // 빌더 패턴 사용
    public static class Builder {
        // 필드와 메서드 구현
    }
}