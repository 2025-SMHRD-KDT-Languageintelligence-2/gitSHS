package com.example.WITHUS.Repository;


import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.Team;
import com.example.WITHUS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CroomRepository extends JpaRepository<Croom, Integer> {
    Optional<Croom> findByCroomTitle(String croomTitle);  // 방 제목으로 중복 체크용
    List<Croom> findByUser(User user); // 유저가 만든 채팅방 목록
    // 추가: 채팅방에 초대된 유저들 조회
    List<Croom> findByInvitedUsersContaining(User user); // 특정 유저가 초대된 채팅방을 가져옴
    Optional<Croom> findByTeamIdx(Team team);
//    void deleteAllByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat ch WHERE ch.croomIdx.teamIdx.userId = :user")
    void deleteAllByTeamUser(@Param("user") User user);

    @Query("SELECT c FROM Croom c WHERE c.teamIdx.userId = :user")
    List<Croom> findAllByTeamUser(@Param("user") User user);

    
    // 계정 탈퇴 시 팀 삭제
    List<Croom> findAllByTeamIdx(Team team);

}