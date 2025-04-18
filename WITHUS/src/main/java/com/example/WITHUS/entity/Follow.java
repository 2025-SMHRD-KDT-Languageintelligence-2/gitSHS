package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;


@Entity
@Table(name = "TB_FOLLOW")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOLLOW_IDX")
    private Long followIdx;

    @Column(name = "FOLLOWER", nullable = false)
    private String follower;

    @Column(name = "FOLLOWEE", nullable = false)
    private String followee;

    @Column(name = "FOLLOWED_AT")
    private String followedAt;



}