package com.example.kanban.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.kanban.dto.TaskWebSocketDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketTaskListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleTaskStatusChange(TaskStatusEvent event) {
        // Map Event -> DTO
        TaskWebSocketDTO dto = TaskWebSocketDTO.builder()
                .taskId(event.taskId())
                .action("STATUS_CHANGED")
                .taskTitle(event.taskTitle())
                .newStatus(event.newStatus())
                .updatedBy(event.updatedBy() != null ? event.updatedBy().getName() : "Unassigned")
                .build();

        messagingTemplate.convertAndSend("/topic/tasks", dto);
    }
}