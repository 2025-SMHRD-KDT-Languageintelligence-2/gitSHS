package com.example.WITHUS.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Transient;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="tb_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Croom> crooms = new HashSet<>();

    @OneToMany(mappedBy = "chatter")
    @JsonIgnore
    private Set<Chat> chats = new HashSet<>();

    @Id
    @Column(name = "USER_ID", length = 50)
    private String userId;

    @Column(name = "USER_PASSWORD", length = 100, nullable = false)
    private String userPassword;

    @Column(name = "USER_NAME", length = 50, nullable = false)
    private String userName;

    @Column(name = "USER_EMAIL", length = 50, nullable = false)
    private String userEmail;

    @Column(name = "USER_BIRTHDATE")
    private Date userBirthdate;

    @Column(name = "JOINED_AT", nullable = false)
    private Timestamp joinedAt;

    @Column(name = "USER_NICK", length = 50, nullable = false)
    private String userNick;

    @Column(name = "USER_SKILL", nullable = false, length = 500)
    private String userSkill;

    @Column(name = "USER_REGION", nullable = false, length = 100)
    private String userRegion;

    @Column(name = "USER_TARGET", nullable = false, length = 100)
    private String userTarget;

    @Column(name = "PROFILE_INFO", length = 1000, nullable = true)
    private String profileInfo;

    @Column(name = "PROFILE_IMG", length = 1000, nullable = true)
    private String profileImg;


}




