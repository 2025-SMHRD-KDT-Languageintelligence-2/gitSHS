package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.ChatRepository;
import com.example.WITHUS.Repository.CroomRepository;
import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.config.JwtUtil;
import com.example.WITHUS.dto.ChatMessageDto;
import com.example.WITHUS.dto.ChatRoomDto;
import com.example.WITHUS.dto.CroomDto;
import com.example.WITHUS.dto.UserFollowDto;
import com.example.WITHUS.entity.Chat;
import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/croom")
@RequiredArgsConstructor
public class CroomController {

    private final CroomRepository croomRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;



    // ✅ 채팅방 생성 API
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Map<String, Object> request) {
        String creatorUserId = (String) request.get("creatorUserId");

        if (creatorUserId == null || creatorUserId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ 생성자 유저 ID 없음"));
        }

        Optional<User> creatorOpt = userRepository.findById(creatorUserId);
        if (creatorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "❌ 생성자 유저 정보 없음"));
        }

        User creator = creatorOpt.get();
        Croom newRoom = new Croom();
        newRoom.setUser(creator);
        newRoom.setCreatedAt(Instant.now());
        newRoom.setCroomStatus("active");
        newRoom.setCroomLimit(10); // 기본 인원 제한
        newRoom.setCroomTitle(creator.getUserNick() + "의 채팅방");

        croomRepository.save(newRoom);

        return ResponseEntity.ok(Map.of(
                "id", newRoom.getId(),
                "title", newRoom.getCroomTitle()
        ));
    }


    // ✅ 채팅방에 사용자 초대 API
    @PostMapping("/invite")
    public ResponseEntity<String> inviteUsers(@RequestBody Map<String, Object> request) {
        Object chatroomIdObj = request.get("chatroomId");
        List<String> userIds = (List<String>) request.get("userIds");

        if (chatroomIdObj == null || userIds == null || userIds.isEmpty()) {
            return ResponseEntity.status(400).body("❌ 유효하지 않은 입력값");
        }

        Integer chatroomId = Integer.parseInt(chatroomIdObj.toString());
        Optional<Croom> roomOpt = croomRepository.findById(chatroomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 채팅방을 찾을 수 없습니다.");
        }

        Croom room = roomOpt.get();
        Set<User> invited = room.getInvitedUsers();
        if (invited == null) invited = new HashSet<>();

        List<String> nickList = new ArrayList<>();

        for (String userId : userIds) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // ✅ 중복 초대 방지
                boolean alreadyInvited = invited.stream()
                        .anyMatch(u -> u.getUserId().equals(user.getUserId()));

                if (!alreadyInvited) {
                    invited.add(user);
                    nickList.add(user.getUserNick());
                }
            }
        }

        room.setInvitedUsers(invited);

        // 제목 업데이트 (옵션)
        if (!nickList.isEmpty()) {
            String newTitle = String.join(", ", nickList) + "의 채팅방";
            room.setCroomTitle(newTitle);
        }

        croomRepository.save(room);

        return ResponseEntity.ok("✅ 초대 성공");
    }

    // 채팅방에 참여하고 있는 유저 목록 API
    @GetMapping("/{croomId}/participants")
    public ResponseEntity<List<UserFollowDto>> getParticipants(@PathVariable Integer croomId) {
        Optional<Croom> roomOpt = croomRepository.findById(croomId);
        if (roomOpt.isEmpty()) return ResponseEntity.status(404).build();

        Croom room = roomOpt.get();
        Set<User> users = new HashSet<>(room.getInvitedUsers());
        users.add(room.getUser());

        List<UserFollowDto> result = users.stream().map(user -> UserFollowDto.builder()
                .userId(user.getUserId())
                .userNick(user.getUserNick())
                .profileImg(user.getProfileImg())
                .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }



    // ✅ 로그인 사용자가 참여 중인 채팅방 목록 조회 API
    @GetMapping("/myrooms")
    public ResponseEntity<List<ChatRoomDto>> getMyRooms(@RequestParam String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        }

        List<Croom> allRooms = croomRepository.findAll();

        List<ChatRoomDto> result = allRooms.stream()
                .filter(room -> {
                    try {
                        // 생성자 또는 초대된 유저 중 포함 여부 체크
                        return room.getUser().getUserId().equals(userId)
                                || room.getInvitedUsers().stream().anyMatch(u -> u.getUserId().equals(userId));
                    } catch (EntityNotFoundException e) {
                        System.out.println("❌ 필터링 중 유저 없음: " + e.getMessage());
                        return false;
                    }
                })
                .map(room -> {
                    try {
                        User creator = room.getUser();
                        String creatorUserId = creator.getUserId();
                        String creatorNick = creator.getUserNick();
                        String creatorImg = creator.getProfileImg();

                        List<String> participantNicks = new ArrayList<>();
                        participantNicks.add(creatorNick);

                        for (User u : room.getInvitedUsers()) {
                            try {
                                participantNicks.add(u.getUserNick());
                            } catch (EntityNotFoundException e) {
                                System.out.println("❌ 초대 유저 정보 없음: " + e.getMessage());
                            }
                        }

                        List<Chat> chats = chatRepository.findByCroomIdxOrderByCreatedAtAsc(room);
                        Chat lastChat = chats.isEmpty() ? null : chats.get(chats.size() - 1);

                        return ChatRoomDto.builder()
                                .croomId(room.getId())
                                .croomTitle(room.getCroomTitle())
                                .creatorUserId(creatorUserId)
                                .creatorProfileImg(creatorImg)
                                .participantNicks(participantNicks)
                                .lastMessage(lastChat != null ? lastChat.getChatContent() : null)
                                .lastMessageTime(lastChat != null ? lastChat.getCreatedAt() : null)
                                .isTeamRoom(room.getTeamIdx() != null)
                                .build();

                    } catch (EntityNotFoundException e) {
                        System.out.println("❌ 방 생성자 정보 없음: " + e.getMessage());
                        return null; // 방 자체 무시
                    }
                })
                .filter(Objects::nonNull) // null 제거
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ 같은 유저와 참여중인 유저가 있는지 조회
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> checkExistingRoom(@RequestParam List<String> userIds) {
        List<Croom> allRooms = croomRepository.findAll();
        Set<String> target = new HashSet<>(userIds);

        for (Croom room : allRooms) {
            Set<String> roomUsers = new HashSet<>();
            roomUsers.add(room.getUser().getUserId()); // 생성자
            roomUsers.addAll(
                    room.getInvitedUsers().stream()
                            .map(User::getUserId)
                            .collect(Collectors.toSet())
            );
            if (roomUsers.equals(target)) {
                return ResponseEntity.ok(Map.of(
                        "exists", true,
                        "roomId", room.getId()
                ));
            }
        }

        return ResponseEntity.ok(Map.of("exists", false));
    }

    // ✅ 특정 채팅방의 채팅 메시지 전체 조회 API
    @GetMapping("/{croomId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(@PathVariable Integer croomId) {
        // 1️⃣ 채팅방 존재 여부 확인
        Optional<Croom> roomOpt = croomRepository.findById(croomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        // 2️⃣ 채팅 목록 가져오기 (오름차순 정렬)
        List<Chat> chats = chatRepository.findByCroomIdxOrderByCreatedAtAsc(roomOpt.get());

        // 3️⃣ Chat → ChatMessageDto 변환
        List<ChatMessageDto> result = chats.stream()
                .map(chat -> ChatMessageDto.builder()
                        .croomIdx(chat.getCroomIdx().getId())                 // 채팅방 ID
                        .senderId(chat.getChatter().getUserId())              // userId (내 메시지 여부 판별용)
                        .sender(chat.getChatter().getUserNick())              // 닉네임 (화면 표시용)
                        .content(chat.getChatContent())                       // 메시지 본문
                        .createdAt(chat.getCreatedAt().toString())            // 생성일시
                        .build()
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }




    // ✅ 채팅 메시지 전송 API
    @PostMapping("/{croomIdx}/chat")
    public ResponseEntity<ChatMessageDto> sendChat(@PathVariable Integer croomIdx, @RequestBody ChatMessageDto msg) {
        // 채팅방 ID(croomIdx)로 채팅방 조회
        Croom croom = croomRepository.findById(croomIdx).orElse(null);
        // 발신자 ID(senderId)로 유저 정보 조회
        User user = userRepository.findById(msg.getSenderId()).orElse(null);

        // 채팅방이나 유저 정보가 없을 경우 에러 반환
        if (croom == null || user == null) {
            return ResponseEntity.status(403).body(null);
        }

        // Chat 객체 생성 및 필드 세팅
        Chat chat = new Chat();
        chat.setCroomIdx(croom);                 // 채팅방
        chat.setChatter(user);                   // 보낸 사람
        chat.setChatContent(msg.getContent());   // 채팅 내용
        chat.setCreatedAt(Instant.now());        // 전송 시각
        // DB에 채팅 저장
        chatRepository.save(chat);

        // 채팅 메시지를 보낸 유저의 닉네임을 포함하여 반환
        ChatMessageDto responseMsg = ChatMessageDto.builder()
                .croomIdx(chat.getCroomIdx().getId())
                .senderId(chat.getChatter().getUserId())  // 유저 ID
                .sender(chat.getChatter().getUserNick())  // 유저 닉네임 추가
                .content(chat.getChatContent())
                .createdAt(chat.getCreatedAt().toString())
                .build();

        // 성공적으로 채팅을 보낸 후 해당 채팅 메시지와 유저 닉네임을 반환
        return ResponseEntity.ok(responseMsg);
    }




    // ✅ 채팅방 나가기 API (채팅방 생성자 / 참여자)
    @DeleteMapping("/{croomId}/exit")
    public ResponseEntity<String> exitOrDeleteRoom(
            @PathVariable Integer croomId,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String userId
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(403).body("❌ 인증 토큰 없음");
        }

        String token = authHeader.substring(7);
        String requesterId = jwtUtil.extractUserId(token);

        Optional<Croom> roomOpt = croomRepository.findById(croomId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (roomOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 채팅방 또는 유저 정보 없음");
        }

        Croom room = roomOpt.get();
        User user = userOpt.get();


        if (!requesterId.equals(userId)) {
            return ResponseEntity.status(403).body("❌ 자신의 계정으로만 나가기 가능합니다.");
        }

        // ✅ 생성자이면 → 삭제 처리
        if (room.getUser().getUserId().equals(requesterId)) {
            // 채팅 삭제
            List<Chat> chats = chatRepository.findByCroomIdxOrderByCreatedAtAsc(room);
            chatRepository.deleteAll(chats);

            // 채팅방 삭제
            croomRepository.delete(room);
            return ResponseEntity.ok("✅ 채팅방 삭제 완료");
        }

// ✅ 초대된 유저이면 → 나가기 처리
        Set<User> invited = room.getInvitedUsers();
        if (invited.contains(user)) {
            invited.remove(user);
            room.setInvitedUsers(invited);

            // 🔍 나가고 나면 참여자가 아무도 안 남는 경우 → 방 삭제
            if (invited.isEmpty()) {
                List<Chat> chats = chatRepository.findByCroomIdxOrderByCreatedAtAsc(room);
                chatRepository.deleteAll(chats);
                croomRepository.delete(room);
                return ResponseEntity.ok("✅ 마지막 유저 퇴장 → 채팅방 삭제 완료");
            }

            // 🔁 참여자 남아 있으면 그냥 저장
            croomRepository.save(room);
            return ResponseEntity.ok("✅ 채팅방 나가기 완료");
        }

        return ResponseEntity.status(403).body("❌ 나가기/삭제 권한이 없습니다.");
    }

    
    
    // ✅ 채팅방 제목 수정
// ✅ 채팅방 제목 수정 API
    @PutMapping("/{roomId}/title")
    public ResponseEntity<?> updateTitle(
            @PathVariable Integer roomId,
            @RequestBody Map<String, Object> body
    ) {
        // 🔍 파라미터 추출
        String newTitle = (String) body.get("newTitle");
        Boolean isAuto = (Boolean) body.get("isAutoTitle"); // 사용 여부에 따라 저장 가능

        if (newTitle == null || newTitle.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ newTitle 파라미터가 없습니다.");
        }

        // ✅ 채팅방 존재 여부 확인
        Optional<Croom> roomOpt = croomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 채팅방이 존재하지 않습니다.");
        }

        // ✅ 제목 수정 및 저장
        Croom room = roomOpt.get();
        room.setCroomTitle(newTitle);
        room.setAutoTitle(isAuto != null ? isAuto : false); // null 방지
        croomRepository.save(room);

        return ResponseEntity.ok("✅ 채팅방 제목이 수정되었습니다.");
    }

    // ✅ 채팅방 제목 수정 후 조회용 API
// ✅ 채팅방 제목 수정 후 조회용 API
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable Integer roomId) {
        Optional<Croom> roomOpt = croomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 채팅방을 찾을 수 없습니다.");
        }

        Croom room = roomOpt.get();

        // ✅ ChatRoomDto로 변환
        ChatRoomDto dto = ChatRoomDto.builder()
                .croomId(room.getId())
                .croomTitle(room.getCroomTitle())
                .creatorUserId(room.getUser().getUserId())
                .creatorProfileImg(room.getUser().getProfileImg())
                .teamId(room.getTeamIdx() != null ? room.getTeamIdx().getTeamIdx() : null)
                .participantNicks(room.getInvitedUsers().stream().map(User::getUserNick).toList())
                .build();

        return ResponseEntity.ok(dto);
    }

}