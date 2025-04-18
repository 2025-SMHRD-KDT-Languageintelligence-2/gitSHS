package com.example.WITHUS.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter

public class CommentDto {
    private Integer cmtIdx;
    private Integer commIdx;
    private String cmtContent;
    private Timestamp createdAt;
    private String userId;
}
