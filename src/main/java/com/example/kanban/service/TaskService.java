package com.example.kanban.service;

import com.example.kanban.dto.TaskDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.entity.Status;
import com.example.kanban.entity.Task;
import com.example.kanban.entity.User;
import com.example.kanban.event.TaskStatusEvent;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.StatusRepository;
import com.example.kanban.repository.TaskRepository;
import com.example.kanban.repository.UserRepository;
import com.example.kanban.service.validation.TaskValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final StatusRepository statusRepository;
    private final TaskValidator taskValidator;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Task createTask(TaskDTO taskDTO) {
        // 1. Validate and fetch entities first
        User user = null;
        if (taskDTO.getUserId() != null) {
            user = userRepository.findById(taskDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + taskDTO.getUserId()));
        }

        // 2. Handle Status with Default Logic
        Status status;
        if (taskDTO.getStatusId() == null) {
            // Fetch the default "To Do" status.
            // Ensure "To Do" exists in your DB as the first in the chain.
            status = statusRepository.findByName("To Do")
                    .orElseThrow(() -> new EntityNotFoundException("Default status 'To Do' not found in database"));
        } else {
            status = statusRepository.findById(taskDTO.getStatusId())
                    .orElseThrow(() -> new EntityNotFoundException("Valid Status ID is required"));
        }

        Sprint sprint = null;
        if (taskDTO.getSprintId() != null) {
            sprint = sprintRepository.findById(taskDTO.getSprintId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Sprint not found with ID: " + taskDTO.getSprintId()));
        }

        // 2. Build the object in one atomic step
        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .user(user)
                .status(status)
                .sprint(sprint)
                .build();

        Task savedTask = taskRepository.save(task);
        eventPublisher.publishEvent(new TaskStatusEvent(savedTask.getId(), savedTask.getTitle(), "NONE", "TODO", user));
        return savedTask;
    }

    // PUT: Full Update (Replacement)
    public Task updateTask(UUID taskId, TaskDTO taskDTO) {
        Task existingTask = findTask(taskId);

        // We replace everything. If DTO fields are null, the DB becomes null.
        Task updatedTask = existingTask.toBuilder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .dueDate(taskDTO.getDueDate())
                .user(taskDTO.getUserId() != null ? fetchUser(taskDTO.getUserId()) : null)
                .status(taskDTO.getStatusId() != null ? fetchStatus(taskDTO.getStatusId()) : null)
                .sprint(taskDTO.getSprintId() != null ? fetchSprint(taskDTO.getSprintId()) : null)
                .build();

        return taskRepository.save(updatedTask);
    }

    // PATCH: Partial Update (Selective)
    @Transactional
    public Task patchTask(UUID taskId, Map<String, Object> updates) {
        // 1. Find the existing record (check if soft-deleted first)
        Task existingTask = taskRepository.findById(taskId).orElse(null);
        
        // If not found, check if it's soft-deleted
        if (existingTask == null) {
            Task deletedTask = taskRepository.findByIdIncludingDeleted(taskId);
            if (deletedTask != null) {
                throw new EntityNotFoundException("Task is soft-deleted. Please restore it first using PUT /api/v1/task/{id}/restore");
            }
            throw new EntityNotFoundException("Task not found with ID: " + taskId);
        }

        // 2. Convert the existing entity to a Map
        // This allows us to "merge" the incoming changes into it
        Map<String, Object> existingMap = objectMapper.convertValue(existingTask, Map.class);

        // 3. Extract relationship fields before merging (they need special handling)
        // Remove from both maps to avoid deserialization issues
        Object userIdUpdate = updates.remove("userId");
        Object statusIdUpdate = updates.remove("statusId");
        Object sprintIdUpdate = updates.remove("sprintId");
        
        // Also remove from existingMap (they're UUID strings from serialization)
        existingMap.remove("userId");
        existingMap.remove("statusId");
        existingMap.remove("sprintId");

        // 4. Apply the remaining updates from the request onto the existing map
        updates.forEach(existingMap::put);

        // 5. Convert the merged map back into a Task object
        // Note: Relationship fields are excluded to avoid deserialization issues
        Task updatedTask = objectMapper.convertValue(existingMap, Task.class);

        // 6. Handle relationship fields separately (they need to be fetched from DB)
        // If updated, use the new value; otherwise preserve existing relationships
        if (userIdUpdate != null) {
            UUID userId = UUID.fromString(userIdUpdate.toString());
            updatedTask.setUser(fetchUser(userId));
        } else {
            // Preserve existing user relationship
            updatedTask.setUser(existingTask.getUser());
        }
        
        if (statusIdUpdate != null) {
            UUID newStatusId = UUID.fromString(statusIdUpdate.toString());
            Logger.getLogger(TaskService.class.getName()).info("Patching Task Status to ID: " + newStatusId);
            Status newStatus = statusRepository.findById(newStatusId)
                    .orElseThrow(() -> new EntityNotFoundException("Status not found: " + newStatusId));
            // Validate status transition
            taskValidator.validateTransition(existingTask, newStatus);
            updatedTask.setStatus(newStatus);
        } else {
            // Preserve existing status relationship
            updatedTask.setStatus(existingTask.getStatus());
        }
        
        if (sprintIdUpdate != null) {
            UUID sprintId = UUID.fromString(sprintIdUpdate.toString());
            updatedTask.setSprint(fetchSprint(sprintId));
        } else {
            // Preserve existing sprint relationship
            updatedTask.setSprint(existingTask.getSprint());
        }

        Logger.getLogger(TaskService.class.getName()).info("Patched Task: " + updatedTask);

        // 7. Validate and save
        taskValidator.validate(updatedTask);

        Task savedTask = taskRepository.save(updatedTask);
        if (savedTask.getStatus() != null
                && !savedTask.getStatus().getName().equals(existingTask.getStatus().getName())) {
            eventPublisher.publishEvent(new TaskStatusEvent(
                    savedTask.getId(),
                    existingTask.getStatus().getName(), // oldStatus
                    savedTask.getStatus().getName(), // newStatus
                    savedTask.getTitle(), // taskTitle
                    savedTask.getUser() // updatedBy
            ));
        }

        return savedTask;
    }

    public Page<Task> getTasks(String title, UUID statusId, Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (title != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (statusId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status").get("id"), statusId));
        }

        // findAll returns a Page object which includes total elements, total pages, and
        // the current slice
        return taskRepository.findAll(spec, pageable);
    }

    public List<Task> getTasksBySprint(UUID sprintId) {
        Sprint sprint = fetchSprint(sprintId);
        return taskRepository.findBySprint(sprint);
    }

    public List<Task> getTasksByUser(UUID userId) {
        User user = fetchUser(userId);
        return taskRepository.findByUser(user);
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        taskRepository.delete(task); // This triggers the @SQLDelete update

        // Fire an event so our listener captures the "Delete" in ActivityLog
        eventPublisher.publishEvent(new TaskStatusEvent(
                task.getId(),
                task.getTitle(),
                task.getStatus().getName(),
                "DELETED",
                task.getUser()));
    }

    public void restoreTask(UUID id) {
        // We check if it exists in the 'trash' first
        taskRepository.restoreById(id);

        // Fire an event to log that it was restored!
        Task restoredTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not verify restoration"));

        eventPublisher.publishEvent(new TaskStatusEvent(
                restoredTask.getId(),
                restoredTask.getTitle(),
                "DELETED",
                restoredTask.getStatus().getName(),
                null));
    }
    
    public List<Task> getDeletedTasks() {
        return taskRepository.findAllDeleted();
    }
    // --- Private Helper Methods to avoid repetition ---

    private Task findTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
    }

    private User fetchUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private Status fetchStatus(UUID id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Status not found: " + id));
    }

    private Sprint fetchSprint(UUID id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + id));
    }
}
