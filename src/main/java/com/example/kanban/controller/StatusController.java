package com.example.kanban.controller;

import com.example.kanban.dto.StatusDTO;
import com.example.kanban.entity.Status;
import com.example.kanban.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
@Tag(name = "Status", description = "APIs for managing task statuses")
public class StatusController {

    @Autowired
    StatusService statusService;

    @Operation(summary = "Create status", description = "Create a new task status")
    @PostMapping
    ResponseEntity<Status> createStatus(@RequestBody StatusDTO statusDTO) {
        return  ResponseEntity.ok(statusService.createStatus(statusDTO));
    }

    @Operation(summary = "List statuses", description = "Get all task statuses")
    @GetMapping
    ResponseEntity<List<Status>> getAllStatus() {
        return  ResponseEntity.ok(statusService.getAllStatus());
    }

}
