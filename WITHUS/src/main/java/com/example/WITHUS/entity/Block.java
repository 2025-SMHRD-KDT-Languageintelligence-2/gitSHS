package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@Entity
@Table(name="TB_BLOCK")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가하는 기본 키
    @Column(name = "BLOCK_IDX")  // 테이블에서 컬럼명
    private Long blockIdx;

    @Column(name = "BLOCKING_USER_ID", nullable = false)
    private String blockingUserId;  // 차단하는 사람

    @Column(name = "BLOCKED_USER_ID", nullable = false)
    private String blockedUserId;   // 차단당하는 사람

    @Column(name = "BLOCKED_AT", nullable = false)
    private Timestamp blockedAt;    // 차단 시점
}