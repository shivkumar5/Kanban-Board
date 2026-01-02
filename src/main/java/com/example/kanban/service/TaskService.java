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
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TaskService {
   private  final TaskRepository taskRepository;
    private  final UserRepository userRepository;
    private  final SprintRepository sprintRepository;
    private  final StatusRepository statusRepository;
    private final ObjectMapper objectMapper;
    private final TaskValidator taskValidator;


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
                    .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + taskDTO.getSprintId()));
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
        public Task updateTask(Long taskId, TaskDTO taskDTO) {
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
        public Task patchTask(Long taskId, JsonPatch patch) {
            Task existingTask = taskRepository.findById(taskId)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found"));

            try {
                // Apply the patch
                JsonNode target = objectMapper.convertValue(existingTask, JsonNode.class);
                JsonNode patched = patch.apply(target);
                Task updatedTask = objectMapper.treeToValue(patched, Task.class);

                // Validate the patched object before touching the DB
                taskValidator.validate(updatedTask);

                return taskRepository.save(updatedTask);

            } catch (JsonPatchException | JsonProcessingException e) {
                throw new RuntimeException("Invalid Patch Operation", e);
            }
        }

        // --- Private Helper Methods to avoid repetition ---

        private Task findTask(Long id) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + id));
        }

        private User fetchUser(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        }

        private Status fetchStatus(Long id) {
            return statusRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Status not found: " + id));
        }

        private Sprint fetchSprint(Long id) {
            return sprintRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + id));
        }
    }


