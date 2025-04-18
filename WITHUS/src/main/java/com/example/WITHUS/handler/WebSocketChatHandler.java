package com.example.WITHUS.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 채팅방 ID별로 세션들을 관리하는 Map
    private final Map<Integer, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 세션별로 채팅방 ID를 기억
    private final Map<WebSocketSession, Integer> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String query = session.getUri().getQuery(); // 예: roomId=3
        if (query != null && query.startsWith("roomId=")) {
            int croomIdx = Integer.parseInt(query.split("=")[1]);
            sessionRoomMap.put(session, croomIdx);
            roomSessions.putIfAbsent(croomIdx, new HashSet<>());
            roomSessions.get(croomIdx).add(session);
            System.out.println("🔵 연결됨 - 방 " + croomIdx + ": " + session.getId());
        } else {
            System.out.println("⚠️ roomId 없음: 세션 등록 실패 → 메시지 안 감");
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        int croomIdx = jsonNode.get("croomIdx").asInt();  // ✅ 방 번호 읽어옴

        // 방 세션 등록
        sessionRoomMap.put(session, croomIdx);
        roomSessions.putIfAbsent(croomIdx, new HashSet<>());
        roomSessions.get(croomIdx).add(session);

        // 해당 방 세션에만 메시지 전달
        for (WebSocketSession s : roomSessions.get(croomIdx)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(payload));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer roomId = sessionRoomMap.get(session);
        if (roomId != null) {
            roomSessions.getOrDefault(roomId, new HashSet<>()).remove(session);
            sessionRoomMap.remove(session);
            System.out.println("🔴 세션 종료 - 방 " + roomId + ": " + session.getId());
        }
    }
}
