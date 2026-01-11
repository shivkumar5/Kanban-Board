package com.example.kanban.repository;


import com.example.kanban.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TaskRepository  extends JpaRepository<Task, UUID> , JpaSpecificationExecutor<Task> {
    List<Task> findBySprint(com.example.kanban.entity.Sprint sprint);
    List<Task> findByUser(com.example.kanban.entity.User user);

    // 1. Find all deleted tasks (Native Query ignores Hibernate filters)
    @Query(value = "SELECT * FROM tasks WHERE is_deleted = true", nativeQuery = true)
    List<Task> findAllDeleted();

    // 2. Restore a task (Native Query to flip the flag back)
    @Modifying
    @Transactional
    @Query(value = "UPDATE tasks SET is_deleted = false WHERE id = :id", nativeQuery = true)
    void restoreById(@Param("id") UUID id);

    // 3. Find task by ID including soft-deleted (Native Query ignores Hibernate filters)
    @Query(value = "SELECT * FROM tasks WHERE id = :id", nativeQuery = true)
    Task findByIdIncludingDeleted(@Param("id") UUID id);
}
