package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Follow;
import com.example.WITHUS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(String follower, String followee);
    void deleteByFollowerAndFollowee(String follower, String followee);
    long countByFollowee(String followee);
    List<Follow> findByFollowee(String followee);  // 팔로워 리스트
    List<Follow> findByFollower(String follower);  // 팔로잉 리스트

    // 계정 삭제 시 필요
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.follower = :userId OR f.followee = :userId")
    void deleteAllByFollowerOrFollowee(@Param("userId") String userId);


    boolean existsByFollowerAndFollowee(User follower, User followee);
}
