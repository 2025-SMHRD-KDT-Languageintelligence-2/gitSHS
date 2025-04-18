package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Comment;
import com.example.WITHUS.entity.Community;
import com.example.WITHUS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 특정 게시글에 대한 댓글을 조회하는 메서드
    List<Comment> findByCommIdx(Community community);
    void deleteByCommIdx(Community commIdx);

    // 계정 삭제 시 필요
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.userId = :user")
    void deleteAllByUserId(@Param("user") User user);
}