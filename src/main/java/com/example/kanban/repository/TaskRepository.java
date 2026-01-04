package com.example.kanban.repository;


import com.example.kanban.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository  extends JpaRepository<Task, UUID> {
    List<Task> findBySprint(com.example.kanban.entity.Sprint sprint);
    List<Task> findByUser(com.example.kanban.entity.User user);
}
