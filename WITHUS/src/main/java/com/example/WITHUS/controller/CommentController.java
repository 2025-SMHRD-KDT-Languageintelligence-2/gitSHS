package com.example.WITHUS.controller;

import com.example.WITHUS.config.JwtUtil;
import com.example.WITHUS.dto.CommentDto;
import com.example.WITHUS.dto.CommentRequestDto;
import com.example.WITHUS.dto.CommunityRequestDto;
import com.example.WITHUS.entity.Comment;
import com.example.WITHUS.entity.Community;
import com.example.WITHUS.entity.User;
import com.example.WITHUS.Repository.CommentRepository;
import com.example.WITHUS.Repository.CommunityRepository;
import com.example.WITHUS.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final JwtUtil jwtUtil;

    // ✅ 댓글 추가
    @PostMapping("/{communityId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Integer communityId,
                                        @RequestBody CommentRequestDto commentDto,
                                        @RequestHeader("Authorization") String token) {
        String userId;
        try {
            userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("❌ 유효하지 않은 토큰입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ 유저가 존재하지 않습니다."));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("❌ 게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setCmtContent(commentDto.getCmtContent());
        comment.setCommIdx(community);
        comment.setUserId(user);

        commentRepository.save(comment);


        return ResponseEntity.ok("✅ 댓글이 추가되었습니다.");
    }

    // ✅ 댓글 조회
    @GetMapping("/{communityId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Integer communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("❌ 게시글을 찾을 수 없습니다."));

        List<Comment> comments = commentRepository.findByCommIdx(community);

        List<CommentDto> commentDtos = comments.stream().map(comment -> {
            CommentDto dto = new CommentDto();
            dto.setCmtIdx(comment.getCmtIdx());
            dto.setCommIdx(comment.getCommIdx().getCommIdx());
            dto.setCmtContent(comment.getCmtContent());
            dto.setCreatedAt(new Timestamp(comment.getCreatedAt().getEpochSecond() * 1000));
            dto.setUserId(comment.getUserId().getUserId());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(commentDtos);
    }

    // ✅ 게시글 전체 목록
    @GetMapping
    public ResponseEntity<List<CommunityRequestDto>> getAllPosts() {
        List<Community> posts = communityRepository.findAll();

        List<CommunityRequestDto> postDtos = posts.stream().map(post ->
                CommunityRequestDto.builder()
                        .commIdx(post.getCommIdx())
                        .commTitle(post.getCommTitle())
                        .commContent(post.getCommContent())
                        .commFile(post.getCommFile())
                        .createdAt(post.getCreatedAt())
                        .commLikes(post.getCommLikes())
                        .userId(post.getUserId().getUserId())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(postDtos);
    }
}