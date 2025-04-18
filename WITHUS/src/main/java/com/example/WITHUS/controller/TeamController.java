package com.example.WITHUS.controller;

import com.example.WITHUS.Repository.CroomRepository;
import com.example.WITHUS.Repository.TeamRepository;
import com.example.WITHUS.Repository.UserRepository;
import com.example.WITHUS.dto.TeamDto;
import com.example.WITHUS.entity.Contest;
import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.Team;
import com.example.WITHUS.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private CroomRepository croomRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 팀 생성
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestBody TeamDto request) {

        //  필수값 null 체크
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 userId에 해당하는 유저가 존재하지 않습니다.");
        }

        Team team = new Team();
        team.setContIdx(request.getContIdx());
        team.setTeamTitle(request.getTeamTitle());
        team.setTeamInfo(request.getTeamInfo());
        team.setTeamLimit(request.getTeamLimit());
        team.setSkill(request.getSkill());
        team.setRegion(request.getRegion());
        team.setTarget(request.getTarget());
        team.setClosedAt(null);
        team.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        team.setUserId(optionalUser.get());

        Team savedTeam = teamRepository.save(team);

        return ResponseEntity.status(201).body(new TeamDto(savedTeam));
    }


    // ✅ 특정 유저의 팀 목록 조회
    @GetMapping("/contest/{contIdx}")
    public ResponseEntity<List<TeamDto>> getTeamsByContest(@PathVariable Integer contIdx) {
        List<Team> teams = teamRepository.findByContIdx(contIdx);
        List<TeamDto> result = teams.stream().map(TeamDto::new).toList();
        return ResponseEntity.ok(result);
    }


    // ✅ 팀 수정 - 생성자 본인만 수정 가능
    @PutMapping("/update/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Integer teamId, @RequestBody TeamDto request) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            return ResponseEntity.status(404).body("해당 팀이 존재하지 않습니다.");
        }

        Team team = optionalTeam.get();

        //  현재 요청한 유저가 생성자인지 확인
        if (!team.getUserId().getUserId().equals(request.getUserId())) {
            return ResponseEntity.status(403).body("해당 팀을 수정할 권한이 없습니다.");
        }

        //  수정 가능한 항목만 반영
        team.setTeamTitle(request.getTeamTitle());
        team.setTeamInfo(request.getTeamInfo());
        team.setTeamLimit(request.getTeamLimit());
        team.setSkill(request.getSkill());
        team.setRegion(request.getRegion());
        team.setTarget(request.getTarget());

        Team updatedTeam = teamRepository.save(team);

        return ResponseEntity.ok(new TeamDto(updatedTeam));
    }

    // ✅ 팀 삭제
    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Integer teamId, @RequestParam String userId) {
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            return ResponseEntity.status(404).body("해당 팀이 존재하지 않습니다.");
        }

        Team team = optionalTeam.get();
        if (!team.getUserId().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("해당 팀을 삭제할 권한이 없습니다.");
        }

        teamRepository.deleteById(teamId);
        return ResponseEntity.ok("✅ 팀이 성공적으로 삭제되었습니다.");
    }


    // ✅ 팀 채팅방 생성
    @Transactional
    @GetMapping("/checkOrCreateRoom/{teamId}")
    public ResponseEntity<Map<String, Object>> checkOrCreateRoom(@PathVariable Integer teamId, @RequestParam String userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 팀이 존재하지 않습니다."));

        Optional<Croom> existingRoom = croomRepository.findByTeamIdx(team);
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 현재 로그인한 유저 정보 없음"));

        if (existingRoom.isPresent()) {
            Croom room = existingRoom.get();

            // ✅ 현재 인원 수 = 초대된 유저 수
            int currentCount = room.getInvitedUsers().size()+1;
            if (!room.getInvitedUsers().contains(currentUser) && currentCount >= team.getTeamLimit()) {
                return ResponseEntity.status(403).body(Map.of("error", "⚠️ 최대 인원을 초과하여 참여할 수 없습니다."));
            }

            // ✅ 아직 참여자가 아니라면 추가
            if (!room.getInvitedUsers().contains(currentUser)) {
                room.getInvitedUsers().add(currentUser);
                croomRepository.save(room);
            }

            return ResponseEntity.ok(Map.of(
                    "exists", true,
                    "roomId", room.getId(),
                    "title", room.getTeamIdx().getTeamTitle()
            ));
        }

        // ✅ 새 채팅방 생성
        User creator = userRepository.findById(team.getUserId().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("❌ 팀 생성자 유저 정보 없음"));

        Croom newRoom = new Croom();
        newRoom.setCroomStatus("active");
        newRoom.setCroomTitle(team.getTeamTitle());
        newRoom.setCroomLimit(team.getTeamLimit()); // teamLimit을 그대로 채팅방에도 설정
        newRoom.setCreatedAt(Instant.now());
        newRoom.setUser(creator);
        newRoom.setTeamIdx(team);

        // ✅ 현재 로그인한 유저를 참여자로 등록
        Set<User> participants = new LinkedHashSet<>();
        participants.add(currentUser); // 생성자는 user 필드로 들어감
        newRoom.setInvitedUsers(participants);

        Croom savedRoom = croomRepository.save(newRoom);

        return ResponseEntity.ok(Map.of(
                "exists", true,
                "roomId", savedRoom.getId(),
                "title", savedRoom.getTeamIdx().getTeamTitle()
        ));
    }



    // 팀 인원 수
    @GetMapping("/participants/count/{teamId}")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable Integer teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) return ResponseEntity.status(404).body(1); // 기본 1명(생성자)

        Optional<Croom> roomOpt = croomRepository.findByTeamIdx(teamOpt.get());

        if (roomOpt.isPresent()) {
            Croom croom = roomOpt.get();
            // ✅ 생성자가 초대 목록에 포함되어 있는 경우 제외
            long participantCount = croom.getInvitedUsers().stream()
                    .filter(user -> !user.getUserId().equals(croom.getUser().getUserId()))
                    .count();

            int totalCount = (int) participantCount+1; // +1은 생성자
            return ResponseEntity.ok(totalCount);
        } else {
            return ResponseEntity.ok(1); // 채팅방 없으면 생성자만
        }
    }


    // ✅ 강퇴 기능 API
    @Transactional
    @DeleteMapping("/kick/{teamId}")
    public ResponseEntity<?> kickUserFromTeam(
            @PathVariable Integer teamId,
            @RequestParam String userId,
            @RequestParam String targetUserId
    ) {
        // ✅ 디버깅 로그
        System.out.println("🔥 요청된 teamId: " + teamId);
        System.out.println("🔥 요청한 userId: " + userId);
        System.out.println("🔥 강퇴 대상 targetUserId: " + targetUserId);

        // ✅ teamId 유효성 검사
        if (teamId == null) {
            return ResponseEntity.badRequest().body("❌ teamId가 null입니다.");
        }

        // ✅ 팀 조회
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isEmpty()) {
            System.out.println("⚠️ 팀 목록: " + teamRepository.findAll()); // 로그로 전체 팀 목록도 찍기
            return ResponseEntity.status(404).body("❌ 해당 팀이 존재하지 않습니다. teamId: " + teamId);
        }

        Team team = optionalTeam.get();

        // ✅ 요청자 권한 확인
        if (!team.getUserId().getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("❌ 팀장만 팀원을 강퇴할 수 있습니다.");
        }

        // ✅ 채팅방 조회
        Optional<Croom> optionalCroom = croomRepository.findByTeamIdx(team);
        if (optionalCroom.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 해당 팀에 채팅방이 없습니다.");
        }

        Croom croom = optionalCroom.get();

        // ✅ 대상 유저 확인
        Optional<User> optionalTarget = userRepository.findById(targetUserId);
        if (optionalTarget.isEmpty()) {
            return ResponseEntity.status(404).body("❌ 대상 유저가 존재하지 않습니다.");
        }

        User targetUser = optionalTarget.get();

        // ✅ 강퇴 대상이 팀원인지 확인
        if (!croom.getInvitedUsers().contains(targetUser)) {
            return ResponseEntity.status(404).body("⚠️ 대상 유저는 팀에 속해 있지 않습니다.");
        }

        // ✅ 강퇴
        croom.getInvitedUsers().remove(targetUser);
        croomRepository.save(croom);

        return ResponseEntity.ok("✅ " + targetUser.getUserNick() + " 님을 강퇴했습니다.");
    }
}