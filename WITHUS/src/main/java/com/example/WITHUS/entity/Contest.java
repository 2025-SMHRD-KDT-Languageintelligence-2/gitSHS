package com.example.WITHUS.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "TB_CONTEST", schema = "campus_24K_LI2_p2_6")
public class Contest {
    @Id
    @Column(name = "CONT_IDX", nullable = false)
    private Integer contestId;

    @Column(name = "VIEW_COUNT")
    private Integer viewCount;

}