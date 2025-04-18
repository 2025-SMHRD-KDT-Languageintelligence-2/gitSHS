package com.example.WITHUS.controller;

import com.example.WITHUS.entity.Contest;
import com.example.WITHUS.Repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins ="*")
@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
public class ContestController {
    private final ContestRepository contestRepository;

    // 조회수 맵 반환
    @GetMapping("/views")
    public Map<Integer, Integer> getAllViewCounts() {
        List<Contest> contests = contestRepository.findAll();
        return contests.stream()
                .collect(Collectors.toMap(Contest::getContestId, c -> c.getViewCount() != null ? c.getViewCount() : 0));
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> increaseViewCount(@PathVariable Integer id) {
        Optional<Contest> optional = contestRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 해당 공모전이 없습니다.");
        }

        Contest contest = optional.get();
        contest.setViewCount((contest.getViewCount() == null ? 0 : contest.getViewCount()) + 1);
        contestRepository.save(contest);
        return ResponseEntity.ok().build();
    }
}