package com.example.kanban.controller;

import com.example.kanban.dto.TaskDTO;
import com.example.kanban.entity.Task;
import com.example.kanban.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Tasks", description = "APIs for managing tasks")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Operation(summary = "Create task", description = "Create a new task with provided details")
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    @Operation(summary = "List tasks", description = "Get paginated list of tasks with optional filters")
    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID statusId,
            // Default to page 0, size 20, sorted by createdAt descending
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.getTasks(title, statusId, pageable));
    }

    @Operation(summary = "Update task", description = "Full replace an existing task by ID")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID taskId, @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDTO));
    }

    @Operation(summary = "Patch task", description = "Partially update task fields")
    @PatchMapping("/{id}")
    public ResponseEntity<Task> patchTask(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(taskService.patchTask(id, updates));
    }

    @GetMapping("/trash")
    public ResponseEntity<List<Task>> getTrash() {
        return ResponseEntity.ok(taskService.getDeletedTasks());
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable UUID id) {
        taskService.restoreTask(id);
        return ResponseEntity.noContent().build();
    }

}
