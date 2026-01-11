package com.example.kanban.controller;

import com.example.kanban.dto.StatusDTO;
import com.example.kanban.entity.Status;
import com.example.kanban.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@Tag(name = "Status", description = "APIs for managing task statuses in the Kanban board")
public class StatusController {

    @Autowired
    StatusService statusService;

    @Operation(
            summary = "Create a new status",
            description = "Creates a new task status (e.g., 'To Do', 'In Progress', 'Done'). " +
                    "Statuses define the workflow stages for tasks in the Kanban board."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status created successfully",
                    content = @Content(schema = @Schema(implementation = Status.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., missing name)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<Status> createStatus(
            @Parameter(description = "Status details including name", required = true)
            @RequestBody StatusDTO statusDTO) {
        return ResponseEntity.ok(statusService.createStatus(statusDTO));
    }

    @Operation(
            summary = "List all statuses",
            description = "Retrieves a list of all task statuses available in the system. " +
                    "Statuses are used to track the current state of tasks in the Kanban workflow."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statuses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Status.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Status>> getAllStatus() {
        return ResponseEntity.ok(statusService.getAllStatus());
    }

}
