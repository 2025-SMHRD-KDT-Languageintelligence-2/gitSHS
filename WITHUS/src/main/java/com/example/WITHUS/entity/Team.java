package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "TB_TEAM")
@Getter
@Setter
public class Team {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_IDX")
    private Integer teamIdx;

    @Column(name = "CONT_IDX")
    private Integer contIdx;

    @Column(name = "TEAM_TITLE", length = 255)
    private String teamTitle;

    @Column(name = "TEAM_INFO", columnDefinition = "TEXT")
    private String teamInfo;

    @Column(name = "TEAM_LIMIT")
    private Integer teamLimit;

    @Column(name = "SKILL", length = 500)
    private String skill;

    @Column(name = "REGION", length = 100)
    private String region;

    @Column(name = "TARGET", length = 100)
    private String target;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User userId;

    @Column(name = "CLOSED_AT")
    private Timestamp closedAt;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}