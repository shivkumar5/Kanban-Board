package com.example.kanban.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kanban.entity.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    
}
