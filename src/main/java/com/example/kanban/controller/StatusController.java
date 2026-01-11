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
import java.util.UUID;

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

    @Operation(
            summary = "Get status by ID",
            description = "Retrieves a single status by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Status.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Status not found",
                    content = @Content
            )
    })
    @GetMapping("/{statusId}")
    public ResponseEntity<Status> getStatusById(
            @Parameter(description = "Status ID", required = true, example = "e0fa2687-3c1d-4dc6-8217-718f8d11d9c2")
            @PathVariable UUID statusId) {
        return ResponseEntity.ok(statusService.getStatusById(statusId));
    }

    @Operation(
            summary = "Update status",
            description = "Updates an existing status. Since status only has a name field, this replaces the status name."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = Status.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Status not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PutMapping("/{statusId}")
    public ResponseEntity<Status> updateStatus(
            @Parameter(description = "Status ID", required = true, example = "e0fa2687-3c1d-4dc6-8217-718f8d11d9c2")
            @PathVariable UUID statusId,
            @Parameter(description = "Status data with name to update", required = true)
            @RequestBody StatusDTO statusDTO) {
        return ResponseEntity.ok(statusService.updateStatus(statusId, statusDTO));
    }

    @Operation(
            summary = "Delete status",
            description = "Deletes a status from the system. Note: This may fail if there are tasks using this status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Status deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Status not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete status (may be in use by tasks)",
                    content = @Content
            )
    })
    @DeleteMapping("/{statusId}")
    public ResponseEntity<Void> deleteStatus(
            @Parameter(description = "Status ID", required = true, example = "e0fa2687-3c1d-4dc6-8217-718f8d11d9c2")
            @PathVariable UUID statusId) {
        statusService.deleteStatus(statusId);
        return ResponseEntity.noContent().build();
    }

}
