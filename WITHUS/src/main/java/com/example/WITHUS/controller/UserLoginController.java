package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.config.JwtUtil;
import com.example.WITHUS.dto.UserLoginRequestDto;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserLoginController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();

        // 실제 서비스에서는 패스워드를 암호화해서 비교해야 합니다.
        if (!user.getUserPassword().equals(request.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }


        // JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getUserId());


        // 메시지 + 토큰 + userId 담은 응답 Map 생성
        Map<String, String> response = new HashMap<>();
        response.put("message", "✅ 로그인 성공!");
        response.put("token", token);
        response.put("userId", user.getUserId());

        return ResponseEntity.ok(response);
    }
}