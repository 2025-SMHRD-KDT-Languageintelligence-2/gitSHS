package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Block;
import com.example.WITHUS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserBlockRepository extends JpaRepository<Block, Long> {
    // 차단한 유저 목록 조회
    List<Block> findByBlockingUserId(String blockingUserId);

    // 특정 유저가 이미 차단한 유저가 있는지 확인
    boolean existsByBlockingUserIdAndBlockedUserId(String blockingUserId, String blockedUserId);

    // 차단 해제
    void deleteByBlockingUserIdAndBlockedUserId(String blockingUserId, String blockedUserId);

    // 계정 삭제시 필요
//    void deleteAllByBlockingUserIdOrBlockedUserId(User blocking, User blocked);
    @Modifying
    @Transactional
    @Query("DELETE FROM Block b WHERE b.blockingUserId = :userId OR b.blockedUserId = :userId")
    void deleteAllByBlockingUserIdOrBlockedUserId(@Param("userId") String userId);
}