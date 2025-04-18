// UserUpdateController.java (혹은 기존 컨트롤러에 추가해도 됨)
package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.dto.UserProfileUpdateDto;
import com.example.WITHUS.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserProfileUpdateController {

    private final UserRepository userRepository;

    @PostMapping(value = "/update", consumes = {"multipart/form-data"})
    @Transactional
    public ResponseEntity<?> updateUserProfile(
            @RequestPart("user") UserProfileUpdateDto dto,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg
    ) throws IOException {

        Optional<User> userOpt = userRepository.findById(dto.getUserId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ 존재하지 않는 사용자입니다.");
        }

        User user = userOpt.get();

        if (dto.getUserNick() != null) {
            user.setUserNick(dto.getUserNick());
        }

        if (dto.getProfileInfo() != null) {
            user.setProfileInfo(dto.getProfileInfo());
        }

        if (profileImg != null && !profileImg.isEmpty()) {
            String originalFileName = profileImg.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

            if (!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
                return ResponseEntity.badRequest().body("❌ JPG, JPEG, PNG만 업로드 가능합니다.");
            }

            String fileName = UUID.randomUUID() + "_" + originalFileName;
            String uploadDir = "C:/community_uploads/profiles/";
            File uploadPath = new File(uploadDir);

            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File saveFile = new File(uploadDir + fileName);
            profileImg.transferTo(saveFile);

            user.setProfileImg(fileName);  // 새 이미지로 업데이트
        }

        userRepository.save(user);

        return ResponseEntity.ok("✅ 프로필 수정 완료!");
    }
}
