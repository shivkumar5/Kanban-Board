package com.example.kanban.service;

import com.example.kanban.dto.TaskDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.entity.Status;
import com.example.kanban.entity.Task;
import com.example.kanban.entity.User;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.StatusRepository;
import com.example.kanban.repository.TaskRepository;
import com.example.kanban.repository.UserRepository;
import com.example.kanban.service.validation.TaskValidator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.BeanRegistry.Spec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final StatusRepository statusRepository;
    private final TaskValidator taskValidator;
    private final ObjectMapper objectMapper;

    public Task createTask(TaskDTO taskDTO) {
        // 1. Validate and fetch entities first
        // We use .map() to onlsy query the DB if the ID is not null
        User user = Optional.ofNullable(taskDTO.getUserId())
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new EntityNotFoundException("Valid User ID is required"));

        Status status = Optional.ofNullable(taskDTO.getStatusId())
                .flatMap(statusRepository::findById)
                .orElseThrow(() -> new EntityNotFoundException("Valid Status ID is required"));

        // Sprint is often optional (task can be in the backlog)
        // But IF an ID is provided, it MUST exist.
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

        return taskRepository.save(task);
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
    public Task patchTask(UUID taskId, Map<String, Object> updates) {
        // 1. Find the existing record
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        // 2. Convert the existing entity to a Map
        // This allows us to "merge" the incoming changes into it
        Map<String, Object> existingMap = objectMapper.convertValue(existingTask, Map.class);

        // 3. Apply the updates from the request onto the existing map
        updates.forEach(existingMap::put);

        // 4. Convert the merged map back into a Task object
        Task updatedTask = objectMapper.convertValue(existingMap, Task.class);

        // 5. Important: Because convertValue doesn't fetch real DB objects (User,
        // Status),
        // we need to re-verify relationships if they were part of the update.
        if (updates.containsKey("userId")) {
            updatedTask.setUser(fetchUser(UUID.fromString(updates.get("userId").toString())));
        }
        if (updates.containsKey("statusId")) {
            updatedTask.setStatus(fetchStatus(UUID.fromString(updates.get("statusId").toString())));
        }

        // 6. Validate and save
        taskValidator.validate(updatedTask);
        return taskRepository.save(updatedTask);
    }

    public Page<Task> getTasks(String title, UUID statusId, Pageable pageable) {
    Specification<Task> spec = (root, query, cb) -> cb.conjunction();

    if (title != null) {
        spec = spec.and((root, query, cb) -> 
            cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
    }

    if (statusId != null) {
        spec = spec.and((root, query, cb) -> 
            cb.equal(root.get("status").get("id"), statusId));
    }

    // findAll returns a Page object which includes total elements, total pages, and the current slice
    return taskRepository.findAll(spec, pageable);
}

    public List<Task> getTasksBySprint (UUID sprintId) {
        Sprint sprint = fetchSprint(sprintId);
        return taskRepository.findBySprint(sprint);
    }

    public List<Task> getTasksByUser (UUID userId) {
        User user = fetchUser(userId);
        return taskRepository.findByUser(user);
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
