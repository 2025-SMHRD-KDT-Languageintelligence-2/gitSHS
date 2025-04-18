package com.example.WITHUS.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ChatRoomDto {
    private Integer croomId;
    private String croomTitle;
    private Boolean isTeamRoom; // 팀인지 아닌지
    private Boolean isAutoTitle;
    private String creatorUserId;
    private String creatorProfileImg;
    private String invitedUserId; // 🔥 초대한 사람의 ID
    private String lastMessage;
    private Instant lastMessageTime;
    private String invitedUserNick; // ✅ 닉네임 추가
    private List<String> participantNicks; // ✅ 모든 참여자 닉네임 리스트
    private Integer teamId;



}
