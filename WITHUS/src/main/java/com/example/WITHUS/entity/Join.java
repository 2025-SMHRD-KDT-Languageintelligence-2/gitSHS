package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "TB_JOIN", schema = "campus_24K_LI2_p2_6")
public class Join {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOIN_IDX", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEAM_IDX", nullable = false)
    private Team teamIdx;

    @Column(name = "APPLIED_AT", nullable = false)
    private Instant appliedAt;

    @Column(name = "APPROVAL_YN", nullable = false)
    private Character approvalYn;

    @Column(name = "APPROVED_AT", nullable = false)
    private Instant approvedAt;

}