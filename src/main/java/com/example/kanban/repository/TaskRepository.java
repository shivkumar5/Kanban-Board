package com.example.kanban.repository;


import com.example.kanban.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository  extends JpaRepository<Task, UUID> {
}
