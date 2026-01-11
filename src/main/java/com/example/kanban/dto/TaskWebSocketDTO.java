package com.example.kanban.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class TaskWebSocketDTO {
    private UUID taskId;
    private String action;      // e.g., "MOVED", "CREATED", "DELETED"
    private String taskTitle;
    private String newStatus;
    private String updatedBy;
}