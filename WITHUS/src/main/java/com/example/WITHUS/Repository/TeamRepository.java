package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Team;
import com.example.WITHUS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByUserId(User user); // ✅ 사용자 기준 팀 목록 조회
    List<Team> findByContIdx(Integer contIdx);
//    void deleteAllByUserId(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM Team t WHERE t.userId = :user")
    void deleteAllByUserId(@Param("user") User user);
}