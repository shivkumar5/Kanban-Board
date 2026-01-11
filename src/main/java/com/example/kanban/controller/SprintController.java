package com.example.kanban.controller;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/sprint")
@Tag(name = "Sprints", description = "APIs for managing sprints")
public class SprintController {

    @Autowired
    SprintService sprintService;

    @Operation(summary = "Create sprint", description = "Create a new sprint")
    @PostMapping
    ResponseEntity<Sprint> createSprint(@RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.createSprint(sprintDTO));
    }

    @Operation(summary = "List sprints", description = "Get all sprints")
    @GetMapping
    ResponseEntity<Iterable<Sprint>> getAllSprints() {
        return ResponseEntity.ok(sprintService.getAllSprints());
    }

    @Operation(summary = "Get sprint", description = "Get sprint by ID")
    @GetMapping("/{sprintId}")
    ResponseEntity<Sprint> getSprintById(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId) {
        return ResponseEntity.ok(sprintService.getSprintById(sprintId));
    }

    @Operation(summary = "Update sprint", description = "Replace sprint by ID")
    @PutMapping("/{sprintId}")
    ResponseEntity<Sprint> updateSprint(@org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId,
            @RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.updateSprint(sprintId, sprintDTO));
    }

    @Operation(summary = "Patch sprint", description = "Partially update sprint fields")
    @PatchMapping("/{sprintId}")
    ResponseEntity<Sprint> patchSprint(@org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId,
            @RequestBody java.util.Map<String, Object> updates) {
        return ResponseEntity.ok(sprintService.patchSprint(sprintId, updates));
    }
}
