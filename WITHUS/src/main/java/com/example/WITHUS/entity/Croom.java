package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "TB_CROOM")
public class Croom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 생성 설정
    @Column(name = "CROOM_IDX", nullable = false)
    private Integer id;

    @Column(name = "CROOM_TITLE", nullable = false, length = 500)
    private String croomTitle;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "CROOM_LIMIT", nullable = false)
    private Integer croomLimit;

    @Column(name = "CROOM_STATUS", nullable = false, length = 10)
    private String croomStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_IDX")
    private Team teamIdx;

    // ✅ 채팅방 삭제 시 채팅도 같이 삭제되도록 설정
    @OneToMany(mappedBy = "croomIdx", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chat> chats = new LinkedHashSet<>();

    // ✅ 초대된 유저들을 관리할 필드
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "CROOM_INVITES",
            joinColumns = @JoinColumn(name = "CROOM_IDX"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    private Set<User> invitedUsers = new LinkedHashSet<>();


    @Column(name = "is_auto_title")
    private boolean autoTitle;

}
