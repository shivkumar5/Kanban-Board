package com.example.kanban.repository;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {
}
