package com.example.kanban.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.kanban.entity.ActivityLog;
import com.example.kanban.repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivityLogListener {
    private final ActivityLogRepository activityLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Start a fresh transaction for the log
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskStatusEvent(TaskStatusEvent event) {
        // Log the activity
        String userName = (event.updatedBy() != null) ? event.updatedBy().getName() : "System/Unassigned";
        ActivityLog log = ActivityLog.builder()
                .action("TASK_STATUS_CHANGED")
                .entityType("Task")
                .entityId(event.taskId().toString())
                .description("Task '" + event.taskTitle() + "' status changed from '" + event.oldStatus() + "' to '"
                        + event.newStatus() + "' by user '" + userName + "'")
                .oldValue(event.oldStatus())
                .newValue(event.newStatus())
                .build();

        activityLogRepository.save(log);

    }
}
