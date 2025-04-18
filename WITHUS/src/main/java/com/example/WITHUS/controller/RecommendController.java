package com.example.WITHUS.controller;




import com.example.WITHUS.dto.RecommendDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final String PYTHON_API_URL = "https://jdy.onrender.com/api/recommend/teams";  // Python 서버 URL

    @PostMapping
    public ResponseEntity<?> getTeamRecommendations(@RequestBody RecommendDto requestDto) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            System.out.println("🔥 추천 요청 도착");
            System.out.println("📥 유저 JSON: " + mapper.writeValueAsString(requestDto.getUser()));
            System.out.println("📥 팀들 JSON: " + mapper.writeValueAsString(requestDto.getTeams()));

            // 🛠 userSkill 안전하게 파싱
            List<String> parsedSkills = mapper.readValue(
                    requestDto.getUser().getUserSkill(),
                    new TypeReference<List<String>>() {
                    }
            );

            Map<String, Object> user = new HashMap<>();
            user.put("skills", parsedSkills);
            user.put("region", requestDto.getUser().getUserRegion());
            user.put("target", requestDto.getUser().getUserTarget());

            List<Map<String, Object>> teams = new ArrayList<>();
            requestDto.getTeams().forEach(team -> {
                Map<String, Object> t = new HashMap<>();
                t.put("team_id", team.getTeamId());
                t.put("recruitment_skill", team.getSkill());
                t.put("region", team.getRegion());
                t.put("goal", team.getTarget());
                teams.add(t);
            });

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user", user);
            requestBody.put("teams", teams);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.postForEntity(PYTHON_API_URL, requestBody, Object.class);

            System.out.println("🧠 추천 결과: " + mapper.writeValueAsString(response.getBody()));
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            System.out.println("❌ 추천 API 내부 오류 발생");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
