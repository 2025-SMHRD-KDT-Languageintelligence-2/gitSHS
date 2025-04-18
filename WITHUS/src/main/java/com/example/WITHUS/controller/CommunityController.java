package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.CommentRepository;
import com.example.WITHUS.Repository.LikeRepository;
import com.example.WITHUS.config.JwtUtil;
import com.example.WITHUS.dto.CommunityRequestDto;
import com.example.WITHUS.entity.Community;
import com.example.WITHUS.entity.Like;
import com.example.WITHUS.entity.User;
import com.example.WITHUS.Repository.CommunityRepository;
import com.example.WITHUS.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final LikeRepository likeRepository; // LikeRepository를 생성자 주입
    private final CommentRepository commentRepository;

    // 게시글 업로드
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("userId") String userId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));

        String fileName = null;
        if (file != null && !file.isEmpty()) {
            fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String savePath = "C:/community_uploads/" + fileName;
            File saveFile = new File(savePath);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            file.transferTo(saveFile);
        }

        Community comm = new Community();
        comm.setCommTitle(title);
        comm.setCommContent(content);
        comm.setCommFile(fileName);
        comm.setCreatedAt(Instant.now());
        comm.setCommLikes(0);
        comm.setUserId(user);

        communityRepository.save(comm);

        return ResponseEntity.ok("✅ 게시글 등록 성공!");
    }

    // 내 게시물만 불러오기
    @GetMapping("/my")
    public ResponseEntity<List<CommunityRequestDto>> getMyPosts(@RequestParam String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));

        List<Community> posts = communityRepository.findByUserId(user);
        List<CommunityRequestDto> dtoList = posts.stream().map(post -> CommunityRequestDto.builder()
                        .commIdx(post.getCommIdx())
                        .commTitle(post.getCommTitle())
                        .commContent(post.getCommContent())
                        .commFile(post.getCommFile())
                        .createdAt(post.getCreatedAt())
                        .commLikes(post.getCommLikes())
                        .userId(post.getUserId().getUserId())  // userId 가져오기
                        .userNick(post.getUserId().getUserNick())  // userNick 가져오기
                        .build())
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/{commIdx}")
    @Transactional
    public ResponseEntity<?> deletePost(
            @PathVariable Integer commIdx,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 인증 토큰이 필요합니다.");
            }

            String userId = jwtUtil.extractUserId(token.substring(7));
            Optional<Community> optionalPost = communityRepository.findById(commIdx);
            if (optionalPost.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 게시글을 찾을 수 없습니다.");
            }

            Community post = optionalPost.get();

            if (!post.getUserId().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ 삭제 권한이 없습니다.");
            }

            // ✅ 댓글 먼저 삭제
            commentRepository.deleteByCommIdx(post);

            // ✅ 좋아요 삭제
            likeRepository.deleteByCommunity(post);

            // ✅ 게시글 삭제
            communityRepository.delete(post);

            return ResponseEntity.ok("✅ 게시글 삭제 성공!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 서버 오류: " + e.getMessage());
        }
    }


    // 특정 유저의 게시물만 불러오기
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommunityRequestDto>> getPostsByUser(@PathVariable String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));

        List<Community> posts = communityRepository.findByUserId(user);
        List<CommunityRequestDto> dtoList = posts.stream().map(post -> CommunityRequestDto.builder()
                .commIdx(post.getCommIdx())
                .commTitle(post.getCommTitle())
                .commContent(post.getCommContent())
                .commFile(post.getCommFile())
                .createdAt(post.getCreatedAt())
                .commLikes(post.getCommLikes())
                .userId(post.getUserId().getUserId())
                .userNick(post.getUserId().getUserNick())
                .build()).toList();

        return ResponseEntity.ok(dtoList);
    }


    // 게시글 전체 목록
    @GetMapping
    public ResponseEntity<List<CommunityRequestDto>> getAllPosts(@RequestHeader("Authorization") String token) {
        String userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));

        List<Community> posts = communityRepository.findAll();

        List<CommunityRequestDto> dtoList = posts.stream().map(post -> {
            boolean liked = likeRepository.findByUserAndCommunity(currentUser, post).isPresent();
            return CommunityRequestDto.builder()
                    .commIdx(post.getCommIdx())
                    .commTitle(post.getCommTitle())
                    .commContent(post.getCommContent())
                    .commFile(post.getCommFile())
                    .createdAt(post.getCreatedAt())
                    .commLikes(post.getCommLikes())
                    .userId(post.getUserId().getUserId())
                    .userNick(post.getUserId().getUserNick())
                    .liked(liked) // 추가
                    .build();
        }).toList();

        return ResponseEntity.ok(dtoList);
    }

    // 이미지 제공 API
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        File file = new File("C:/community_uploads/" + filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    // 좋아요 추가/삭제
    @PostMapping("/{communityId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Integer communityId,
            @RequestHeader("Authorization") String token) {

        String userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("❌ 게시글을 찾을 수 없습니다."));

        // 좋아요 상태 확인
        Optional<Like> existingLike = likeRepository.findByUserAndCommunity(user, community);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            Like like = new Like();
            like.setUser(user);
            like.setCommunity(community);
            likeRepository.save(like);
        }

        // 최신 좋아요 수 반영
        int updatedLikeCount = (int) likeRepository.countByCommunity(community);
        community.setCommLikes(updatedLikeCount);
        communityRepository.save(community);

        return ResponseEntity.ok("✅ 좋아요 상태가 변경되었습니다.");
    }
}