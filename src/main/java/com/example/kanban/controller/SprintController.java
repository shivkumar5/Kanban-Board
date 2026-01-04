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

@RestController
@RequestMapping("/api/v1/sprint")
public class SprintController {

    @Autowired
    SprintService sprintService;

    @PostMapping
    ResponseEntity<Sprint> createSprint(@RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.createSprint(sprintDTO));
    }

    @GetMapping
    ResponseEntity<Iterable<Sprint>> getAllSprints() {
        return ResponseEntity.ok(sprintService.getAllSprints());
    }

    @GetMapping("/{sprintId}")
    ResponseEntity<Sprint> getSprintById(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId) {
        return ResponseEntity.ok(sprintService.getSprintById(sprintId));
    }

    @PutMapping("/{sprintId}")
    ResponseEntity<Sprint> updateSprint(@org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId,
            @RequestBody SprintDTO sprintDTO) {
        return ResponseEntity.ok(sprintService.updateSprint(sprintId, sprintDTO));
    }

    @PatchMapping("/{sprintId}")
    ResponseEntity<Sprint> patchSprint(@org.springframework.web.bind.annotation.PathVariable java.util.UUID sprintId,
            @RequestBody java.util.Map<String, Object> updates) {
        return ResponseEntity.ok(sprintService.patchSprint(sprintId, updates));
    }
}
