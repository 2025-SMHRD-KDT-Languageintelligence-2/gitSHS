package com.example.WITHUS.Repository;

import com.example.WITHUS.entity.Todo;
import com.example.WITHUS.entity.Croom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByCroom(Croom croom);
}