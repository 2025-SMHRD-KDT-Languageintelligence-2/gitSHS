package com.example.WITHUS.controller;


import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.config.JwtUtil;
import com.example.WITHUS.dto.UserChangePasswordRequestDto;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserChangePasswordController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil; // ✅ JwtUtil 주입

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserChangePasswordRequestDto dto,
            @RequestHeader("Authorization") String tokenHeader
    ) {
        try {
            // ✅ JWT 토큰에서 userId 추출
            String token = tokenHeader.replace("Bearer ", "");
            String userId = jwtUtil.extractUserId(token); // 여기서 userId 추출

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("❌ 사용자를 찾을 수 없습니다."));

            // 현재 비밀번호 비교 (평문으로 비교)
            if (!dto.getCurrentPassword().equals(user.getUserPassword())) {
                return ResponseEntity.badRequest().body("❌ 현재 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호와 확인 값이 일치하는지 확인
            if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
                return ResponseEntity.badRequest().body("❌ 새 비밀번호와 확인 값이 일치하지 않습니다.");
            }

            // 새 비밀번호를 평문으로 저장
            user.setUserPassword(dto.getNewPassword());
            userRepository.save(user);

            return ResponseEntity.ok("✅ 비밀번호가 성공적으로 변경되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ 서버 오류: " + e.getMessage());
        }
    }
}