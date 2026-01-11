package com.example.kanban.event;

import java.util.UUID;

import com.example.kanban.entity.User;

public record TaskStatusEvent( 
    UUID taskId,
    String oldStatus,
    String newStatus,  
    String taskTitle,
    User updatedBy) {} 
