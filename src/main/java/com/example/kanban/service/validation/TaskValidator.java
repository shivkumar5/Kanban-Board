package com.example.kanban.service.validation;

import com.example.kanban.entity.Task;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.StatusRepository;
import com.example.kanban.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskValidator {

    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final SprintRepository sprintRepository;

    public void validate(Task task) {
        // 1. Validate User
        if (task.getUser() != null && task.getUser().getId() != null) {
            if (!userRepository.existsById(task.getUser().getId())) {
                throw new EntityNotFoundException("Assigned User not found with ID: " + task.getUser().getId());
            }
        }

        // 2. Validate Status (Required field for Kanban)
        if (task.getStatus() == null || task.getStatus().getId() == null) {
            throw new IllegalArgumentException("Task must have a valid Status");
        } else if (!statusRepository.existsById(task.getStatus().getId())) {
            throw new EntityNotFoundException("Status not found with ID: " + task.getStatus().getId());
        }

        // 3. Validate Sprint (Optional, but must exist if provided)
        if (task.getSprint() != null && task.getSprint().getId() != null) {
            if (!sprintRepository.existsById(task.getSprint().getId())) {
                throw new EntityNotFoundException("Sprint not found with ID: " + task.getSprint().getId());
            }
        }
    }
}