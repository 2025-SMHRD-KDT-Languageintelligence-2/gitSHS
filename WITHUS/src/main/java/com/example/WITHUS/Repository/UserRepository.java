package com.example.WITHUS.Repository;

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
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String userId); // 필드명과 정확히 일치
    List<User> findByUserNickContainingIgnoreCase(String keyword);
    boolean existsByUserNick(String userNick);

    // 계정 삭제 시 필요
//    void deleteAllByUserId(User user);
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);
}

