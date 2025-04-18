package com.example.WITHUS.controller;


import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.dto.UserSignupDto;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserSignupController {
    private final UserRepository userRepository;


    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    @Transactional
    public ResponseEntity<String> signup(
            @RequestPart("user") UserSignupDto dto,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg
    ) throws IOException {

        // 1. 필수 항목 체크
        if (dto.getUserId() == null || dto.getUserPassword() == null || dto.getUserPasswordCheck() == null ||
                dto.getUserName() == null || dto.getUserEmail() == null || dto.getUserBirthdate() == null ||
                dto.getUserNick() == null || dto.getUserSkill() == null || dto.getUserTarget() == null ||
                dto.getUserRegion() == null) {
            return ResponseEntity.badRequest().body("❌ 모든 항목을 입력해야 합니다.");
        }

        // 2. 비밀번호 일치 여부 확인
        if (!dto.getUserPassword().equals(dto.getUserPasswordCheck())) {
            return ResponseEntity.badRequest().body("❌ 비밀번호가 일치하지 않습니다.");
        }

        // 3. 닉네임 중복 체크
        boolean isDuplicateNick = userRepository.existsByUserNick(dto.getUserNick());
        if (isDuplicateNick) {
            return ResponseEntity.status(409).body("❌ 이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
        }

        // 4. 프로필 이미지 저장
        String savedFileName = null;
        if (profileImg != null && !profileImg.isEmpty()) {
            String originalFileName = profileImg.getOriginalFilename();

            // 파일 확장자 확인 (JPG, PNG만 허용)
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            if (!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
                return ResponseEntity.badRequest().body("❌ 지원하지 않는 파일 형식입니다. JPG, JPEG 또는 PNG 파일만 가능합니다.");
            }

            // 저장할 고유 파일명 생성
            String fileName = UUID.randomUUID() + "_" + originalFileName;

            // 저장 경로 지정
            String uploadDir = "C:/community_uploads/profiles/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs(); // 폴더가 없으면 생성
            }

            // 실제 파일 저장
            File saveFile = new File(uploadDir + fileName);
            profileImg.transferTo(saveFile);

            // 저장된 파일명을 DB에 저장 (이 이름으로 이미지 접근 가능)
            savedFileName = fileName;
        }


        // 3. 엔티티 저장
        User user = User.builder()
                .userId(dto.getUserId())
                .userPassword(dto.getUserPassword())
                .userName(dto.getUserName())
                .userNick(dto.getUserNick())
                .userEmail(dto.getUserEmail())
                .userBirthdate(Date.valueOf(dto.getUserBirthdate()))
                .userRegion(dto.getUserRegion())
                .userSkill(dto.getUserSkill())
                .userTarget(dto.getUserTarget())
                .profileImg(savedFileName) // Base64 대신 파일명 저장
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("✅ 회원가입 성공!");
    }
}

