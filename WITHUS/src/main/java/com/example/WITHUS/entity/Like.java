package com.example.WITHUS.entity;

import com.example.WITHUS.dto.LikeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_LIKE")
@Getter
@Setter
@IdClass(LikeId.class)
public class Like {

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "COMM_ID", nullable = false)
    private Community community;
}
