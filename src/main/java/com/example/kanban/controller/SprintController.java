package com.example.kanban.controller;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sprint")
@Tag(name = "Sprints", description = "APIs for managing sprints in the Kanban system")
public class SprintController {

    @Autowired
    SprintService sprintService;

    @Operation(
            summary = "Create a new sprint",
            description = "Creates a new sprint with the provided details. A sprint is a time-boxed period " +
                    "during which specific work is completed. Each sprint must be associated with a user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sprint created successfully",
                    content = @Content(schema = @Schema(implementation = Sprint.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<Sprint> createSprint(
            @Parameter(description = "Sprint details including name, startDate, endDate, and userId", required = true)
            @RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.createSprint(sprintDTO));
    }

    @Operation(
            summary = "List sprints with pagination and filters",
            description = "Retrieves a paginated list of sprints with optional filtering by name and userId. " +
                    "Results are sorted by createdAt in descending order by default."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sprints retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<Sprint>> getAllSprints(
            @Parameter(description = "Filter sprints by name (partial match, case-insensitive)", example = "Sprint 1")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter sprints by user ID", example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam(required = false) UUID userId,
            @Parameter(description = "Pagination parameters (page, size, sort). Default: page=0, size=20, sort=createdAt,DESC")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(sprintService.getSprints(name, userId, pageable));
    }

    @Operation(
            summary = "Get sprint by ID",
            description = "Retrieves a single sprint by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sprint retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Sprint.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sprint not found",
                    content = @Content
            )
    })
    @GetMapping("/{sprintId}")
    public ResponseEntity<Sprint> getSprintById(
            @Parameter(description = "Sprint ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID sprintId) {
        return ResponseEntity.ok(sprintService.getSprintById(sprintId));
    }

    @Operation(
            summary = "Update sprint (full replace)",
            description = "Performs a full replacement of an existing sprint. All fields in the request body will replace the existing sprint data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sprint updated successfully",
                    content = @Content(schema = @Schema(implementation = Sprint.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sprint not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PutMapping("/{sprintId}")
    public ResponseEntity<Sprint> updateSprint(
            @Parameter(description = "Sprint ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID sprintId,
            @Parameter(description = "Complete sprint data to replace existing sprint", required = true)
            @RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.updateSprint(sprintId, sprintDTO));
    }

    @Operation(
            summary = "Partially update sprint",
            description = "Updates only the specified fields of a sprint. Only include the fields you want to update. " +
                    "Supported fields: name, startDate, endDate, userId"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sprint updated successfully",
                    content = @Content(schema = @Schema(implementation = Sprint.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sprint not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            )
    })
    @PatchMapping("/{sprintId}")
    public ResponseEntity<Sprint> patchSprint(
            @Parameter(description = "Sprint ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID sprintId,
            @Parameter(description = "Map of fields to update (e.g., {\"name\": \"Sprint 2\", \"startDate\": \"2026-01-15\"})", required = true)
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(sprintService.patchSprint(sprintId, updates));
    }

    @Operation(
            summary = "Delete sprint",
            description = "Deletes a sprint from the system. Note: This may fail if there are tasks associated with this sprint."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Sprint deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sprint not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete sprint (may be in use by tasks)",
                    content = @Content
            )
    })
    @DeleteMapping("/{sprintId}")
    public ResponseEntity<Void> deleteSprint(
            @Parameter(description = "Sprint ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID sprintId) {
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.noContent().build();
    }
}
