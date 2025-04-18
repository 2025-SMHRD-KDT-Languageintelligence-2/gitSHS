package com.example.WITHUS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "TB_CHAT", schema = "campus_24K_LI2_p2_6")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_IDX", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CROOM_IDX", nullable = false)
    @JsonIgnore
    private Croom croomIdx;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHATTER", nullable = false)
    @JsonIgnore
    private User chatter;

    @Lob
    @Column(name = "CHAT_CONTENT")
    private String chatContent;

    @Column(name = "CHAT_FILE", length = 1000)
    private String chatFile;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;


}