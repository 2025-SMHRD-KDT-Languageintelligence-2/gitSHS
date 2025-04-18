package com.example.WITHUS.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CroomDto {
    private String title;
    private String userId;
    private Integer limit;
    private String status;
    private String type;
    private Integer teamId;
    private boolean isAutoTitle;
}
