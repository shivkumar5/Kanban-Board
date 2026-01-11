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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Tasks", description = "APIs for managing tasks in the Kanban board")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task with the provided details. If statusId is not provided, defaults to 'To Do' status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User, Status, or Sprint not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<Task> createTask(
            @Parameter(description = "Task details including title, description, dueDate, statusId, userId, and sprintId", required = true)
            @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    @Operation(
            summary = "List tasks with pagination and filters",
            description = "Retrieves a paginated list of tasks with optional filtering by title and statusId. " +
                    "Results are sorted by createdAt in descending order by default."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @Parameter(description = "Filter tasks by title (partial match, case-insensitive)", example = "Implement")
            @RequestParam(required = false) String title,
            @Parameter(description = "Filter tasks by status ID", example = "e0fa2687-3c1d-4dc6-8217-718f8d11d9c2")
            @RequestParam(required = false) UUID statusId,
            @Parameter(description = "Pagination parameters (page, size, sort). Default: page=0, size=20, sort=createdAt,DESC")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.getTasks(title, statusId, pageable));
    }

    @Operation(
            summary = "Update a task (full replace)",
            description = "Performs a full replacement of an existing task. All fields in the request body will replace the existing task data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "Task ID", required = true, example = "f80c297d-9aa3-49c4-a636-307010e6f06c")
            @PathVariable UUID taskId,
            @Parameter(description = "Complete task data to replace existing task", required = true)
            @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDTO));
    }

    @Operation(
            summary = "Partially update a task",
            description = "Updates only the specified fields of a task. Only include the fields you want to update. " +
                    "Status transitions are validated according to business rules."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found or is soft-deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition or request data",
                    content = @Content
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Task> patchTask(
            @Parameter(description = "Task ID", required = true, example = "f80c297d-9aa3-49c4-a636-307010e6f06c")
            @PathVariable UUID id,
            @Parameter(description = "Map of fields to update (e.g., {\"statusId\": \"uuid\", \"title\": \"New Title\"})", required = true)
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(taskService.patchTask(id, updates));
    }

    @Operation(
            summary = "Get soft-deleted tasks",
            description = "Retrieves all tasks that have been soft-deleted (moved to trash). " +
                    "These tasks can be restored using the restore endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of deleted tasks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Task.class))
            )
    })
    @GetMapping("/trash")
    public ResponseEntity<List<Task>> getTrash() {
        return ResponseEntity.ok(taskService.getDeletedTasks());
    }

    @Operation(
            summary = "Restore a soft-deleted task",
            description = "Restores a task that was previously soft-deleted, making it active again in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Task restored successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found in trash",
                    content = @Content
            )
    })
    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restore(
            @Parameter(description = "Task ID to restore", required = true, example = "f80c297d-9aa3-49c4-a636-307010e6f06c")
            @PathVariable UUID id) {
        taskService.restoreTask(id);
        return ResponseEntity.noContent().build();
    }

}
