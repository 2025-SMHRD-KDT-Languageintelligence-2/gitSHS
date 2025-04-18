package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.FollowRepository;
import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.dto.UserFollowDto;
import com.example.WITHUS.entity.Follow;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class UserFollowController {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<String> follow(@RequestBody UserFollowDto dto) {
        if (!followRepository.existsByFollowerAndFollowee(dto.getFollower(), dto.getFollowee())) {
            Follow follow = Follow.builder()
                    .follower(dto.getFollower())
                    .followee(dto.getFollowee())
                    .followedAt(new Timestamp(System.currentTimeMillis()).toString())
                    .build();
            followRepository.save(follow);
            return ResponseEntity.ok("팔로우 성공");
        } else {
            return ResponseEntity.badRequest().body("이미 팔로우 중");
        }
    }

    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<String> unfollow(@RequestBody UserFollowDto dto) {
        if (followRepository.existsByFollowerAndFollowee(dto.getFollower(), dto.getFollowee())) {
            followRepository.deleteByFollowerAndFollowee(dto.getFollower(), dto.getFollowee());
            return ResponseEntity.ok("언팔로우 성공");
        } else {
            return ResponseEntity.badRequest().body("팔로우 상태가 아닙니다");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isFollowing(@RequestParam String follower, @RequestParam String followee) {
        boolean exists = followRepository.existsByFollowerAndFollowee(follower, followee);
        return ResponseEntity.ok(exists);
    }

    // ✅ 팔로워 목록 조회 (나를 팔로우하는 사람)
    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<UserFollowDto>> getFollowers(@PathVariable String userId) {
        List<Follow> followers = followRepository.findByFollowee(userId);

        List<UserFollowDto> result = followers.stream()
                .map(follow -> userRepository.findById(follow.getFollower()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> UserFollowDto.builder()
                        .userId(user.getUserId())
                        .userNick(user.getUserNick())
                        .profileImg(user.getProfileImg())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ 팔로잉 목록 조회 (내가 팔로우한 사람)
    @GetMapping("/followings/{userId}")
    public ResponseEntity<List<UserFollowDto>> getFollowings(@PathVariable String userId) {
        List<Follow> followings = followRepository.findByFollower(userId);

        List<UserFollowDto> result = followings.stream()
                .map(follow -> userRepository.findById(follow.getFollowee()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> UserFollowDto.builder()
                        .userId(user.getUserId())
                        .userNick(user.getUserNick())
                        .profileImg(user.getProfileImg())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }


    // ✅ 맞팔 확인용 API
    @GetMapping("/mutual/{userId}")
    public ResponseEntity<List<UserFollowDto>> getMutualFollows(@PathVariable String userId) {
        List<Follow> followings = followRepository.findByFollower(userId); // 내가 팔로우한 사람들
        List<Follow> followers = followRepository.findByFollowee(userId); // 나를 팔로우한 사람들

        Set<String> followerIds = followers.stream()
                .map(Follow::getFollower)
                .collect(Collectors.toSet());

        List<UserFollowDto> mutuals = followings.stream()
                .map(Follow::getFollowee)
                .filter(followerIds::contains) // 맞팔 관계 필터링
                .map(id -> userRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> UserFollowDto.builder()
                        .userId(user.getUserId())
                        .userNick(user.getUserNick())
                        .profileImg(user.getProfileImg())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(mutuals);
    }

}
