package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.*;
import com.example.WITHUS.dto.UserDeleteAccountRequestDto;
import com.example.WITHUS.entity.Community;
import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.Team;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/delete")
@RequiredArgsConstructor
public class UserDeleteController {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FollowRepository followRepository;
    private final UserBlockRepository blockRepository;
    private final UserRepository joinRepository;
    private final TeamRepository teamRepository;
    private final CroomRepository croomRepository;
    private final ChatRepository chatRepository;
    private final CommunityRepository communityRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<?> deleteAccount(@PathVariable String userId,
                                           @RequestBody UserDeleteAccountRequestDto dto) {
        // 1. 요청 유효성 체크
        if (dto == null || dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return ResponseEntity.badRequest().body("❌ 요청 정보가 잘못되었습니다.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("❌ 비밀번호가 일치하지 않습니다.");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 존재하지 않는 사용자입니다.");
        }

        User user = userOpt.get();

        // 2. 비밀번호 확인 (암호화 안 했으므로 plain 비교)
        if (!dto.getPassword().equals(user.getUserPassword())) {
            return ResponseEntity.badRequest().body("❌ 현재 비밀번호가 올바르지 않습니다.");
        }

        // 1. 좋아요 삭제 (개인 좋아요 먼저)
        likeRepository.deleteAllByUser(user);

        // 🔥 2. 내가 쓴 게시글들에서 달린 좋아요 삭제
        List<Community> myPosts = communityRepository.findByUserId(user);
        for (Community post : myPosts) {
            likeRepository.deleteAllByCommunity(post); // 🔥 게시글에 달린 좋아요 삭제
        }

        // 3. 댓글 삭제
        commentRepository.deleteAllByUserId(user);

        // 4. 커뮤니티 게시글 삭제
        communityRepository.deleteAll(myPosts); // 순서 중요

        // 5. 팀을 참조하는 Croom(채팅방) 먼저 삭제
        List<Croom> userTeamCrooms = croomRepository.findAllByTeamUser(user);
        for (Croom c : userTeamCrooms) {
            chatRepository.deleteByCroomIdx(c);
            croomRepository.delete(c);
        }

// 🔥 팀 삭제 전에 Croom(채팅방) 먼저 제거
        List<Team> teams = teamRepository.findByUserId(user);
        for (Team team : teams) {
            List<Croom> crooms = croomRepository.findAllByTeamIdx(team);
            for (Croom c : crooms) {
                chatRepository.deleteByCroomIdx(c);
                croomRepository.delete(c);
            }
        }

        // 6. 팀 삭제
        teamRepository.deleteAll(teams);

        // (5) 팔로우/차단 삭제
        followRepository.deleteAllByFollowerOrFollowee(user.getUserId());
        blockRepository.deleteAllByBlockingUserIdOrBlockedUserId(user.getUserId());

        // (6) 마지막으로 유저 삭제
        userRepository.delete(user);
        return ResponseEntity.ok("✅ 계정이 성공적으로 삭제되었습니다.");
    }
}