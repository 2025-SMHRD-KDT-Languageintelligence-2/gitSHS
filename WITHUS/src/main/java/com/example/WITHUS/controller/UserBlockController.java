package com.example.WITHUS.controller;

import com.example.WITHUS.dto.UserBlockDto;
import com.example.WITHUS.dto.UserBlockResponseDto;
import com.example.WITHUS.entity.Block;
import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.Repository.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
public class UserBlockController {
    private final UserRepository userRepository;
    private final UserBlockRepository userBlockRepository;

    // 1. 차단하기
    @PostMapping
    public ResponseEntity<String> blockUser(@RequestBody UserBlockDto requestDto) {
        // 차단하려는 사람과 차단당하는 사람이 모두 유효한지 확인
        if (userRepository.findById(requestDto.getBlockingUserId()).isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 차단하는 유저가 유효하지 않습니다.");
        }
        if (userRepository.findById(requestDto.getBlockedUserId()).isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 차단당하는 유저가 유효하지 않습니다.");
        }

        // 이미 차단된 관계인지 확인
        if (userBlockRepository.existsByBlockingUserIdAndBlockedUserId(requestDto.getBlockingUserId(), requestDto.getBlockedUserId())) {
            return ResponseEntity.badRequest().body("❌ 이미 차단한 유저입니다.");
        }

        // 차단 관계 저장
        Block block = new Block();
        block.setBlockingUserId(requestDto.getBlockingUserId());
        block.setBlockedUserId(requestDto.getBlockedUserId());
        block.setBlockedAt(new Timestamp(System.currentTimeMillis()));
        userBlockRepository.save(block);

        return ResponseEntity.ok("✅ 유저를 차단했습니다.");
    }

    // 2. 차단 목록 조회
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<UserBlockResponseDto>> getBlockedList(@PathVariable String userId) {
        List<Block> blockedUsers = userBlockRepository.findByBlockingUserId(userId);

        // 차단된 유저 목록을 DTO로 변환
        List<UserBlockResponseDto> response = blockedUsers.stream().map(block -> {
            UserBlockResponseDto dto = new UserBlockResponseDto();
            dto.setBlockedUserId(block.getBlockedUserId());
            dto.setBlockedAt(block.getBlockedAt().toString());  // 차단 시점 포맷팅 (혹은 필요한 형식으로)
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 3. 차단 해제
    @DeleteMapping("/unblock")
    @Transactional
    public ResponseEntity<String> unblockUser(@RequestBody UserBlockDto requestDto) {
        userBlockRepository.deleteByBlockingUserIdAndBlockedUserId(requestDto.getBlockingUserId(), requestDto.getBlockedUserId());
        return ResponseEntity.ok("🚫 차단 해제되었습니다.");
    }
}
