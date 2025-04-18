package com.example.WITHUS.controller;

import com.example.WITHUS.dto.TodoDto;
import com.example.WITHUS.entity.Croom;
import com.example.WITHUS.entity.Todo;
import com.example.WITHUS.Repository.CroomRepository;
import com.example.WITHUS.Repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoRepository todoRepository;
    private final CroomRepository croomRepository;

    // ✅ 일정 전체 조회 (완료)
    @GetMapping("/{croomId}")
    public ResponseEntity<?> getTodos(@PathVariable Integer croomId) {
        Croom croom = croomRepository.findById(croomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        List<TodoDto> todos = todoRepository.findByCroom(croom).stream()
                .map(todo -> new TodoDto(todo.getId(), todo.getTitle(), todo.getStart(), todo.getEnd()))
                .toList();

        return ResponseEntity.ok(todos);
    }

    // ✅ 일정 생성 (TodoDto → Todo 변환)
    @PostMapping("/{croomId}")
    public ResponseEntity<?> createTodo(@PathVariable Integer croomId, @RequestBody TodoDto dto) {
        Croom croom = croomRepository.findById(croomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setStart(dto.getStart());
        todo.setEnd(dto.getEnd());
        todo.setCroom(croom);

        Todo saved = todoRepository.save(todo);
        return ResponseEntity.ok(new TodoDto(saved.getId(), saved.getTitle(), saved.getStart(), saved.getEnd()));
    }

    // ✅ 일정 삭제 → JSON 형식 응답
    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long todoId) {
        todoRepository.deleteById(todoId);
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}