package com.example.WITHUS.dto;

import com.example.WITHUS.entity.Todo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // ✅ 기본 생성자 (필수)
@AllArgsConstructor // ✅ 전체 필드 생성자
public class TodoDto {
    private Long id;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
}