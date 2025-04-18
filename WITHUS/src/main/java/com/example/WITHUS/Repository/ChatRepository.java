package com.example.WITHUS.Repository;


import com.example.WITHUS.entity.Chat;
import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.Team;
import com.example.WITHUS.entity.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    // 특정 채팅방에 속한 모든 채팅을 생성 시간 순으로 가져옴
    List<Chat> findByCroomIdxOrderByCreatedAtAsc(Croom croom);

    // 채팅방에서 최신 메시지 하나를 가져옴
    Optional<Chat> findTop1ByCroomIdxOrderByCreatedAtDesc(Croom croom); // 최신 메시지 하나

    @Modifying
    @Transactional
    void deleteByCroomIdx(Croom croom);


}
