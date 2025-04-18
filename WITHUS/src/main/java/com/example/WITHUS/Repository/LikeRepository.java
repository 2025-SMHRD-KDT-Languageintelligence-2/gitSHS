package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Like;
import com.example.WITHUS.dto.LikeId;
import com.example.WITHUS.entity.User;
import com.example.WITHUS.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {
    // 유저와 게시글로 좋아요 여부 확인
    Optional<Like> findByUserAndCommunity(User user, Community community);

    // 특정 게시글의 좋아요 수 세기
    long countByCommunity(Community community);

    void deleteByCommunity(Community community);

    // 계정 삭제 시 필요
//    void deleteAllByUser(User user);

    void deleteAllByCommunity(Community community);

        @Modifying
        @Transactional
        @Query("DELETE FROM Like l WHERE l.user = :user")
        void deleteAllByUser(@Param("user") User user);
}