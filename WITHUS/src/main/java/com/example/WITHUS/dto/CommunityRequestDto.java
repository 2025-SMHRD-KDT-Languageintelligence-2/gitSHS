package com.example.WITHUS.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityRequestDto {
    private Integer commIdx;
    private String commTitle;
    private String commContent;
    private String commFile;
    private Instant createdAt;
    private Integer commLikes;
    private String userNick;
    private String userId;
    private boolean liked;
}
