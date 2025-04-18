package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "TB_COMMENT", schema = "campus_24K_LI2_p2_6")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CMT_IDX", nullable = false)
    private Integer cmtIdx;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMM_IDX", nullable = false)
    private Community commIdx;

    @Column(name = "CMT_CONTENT", nullable = false, length = 900)
    private String cmtContent;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User userId;

    // ✅ 댓글 저장 전에 자동으로 createdAt 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}