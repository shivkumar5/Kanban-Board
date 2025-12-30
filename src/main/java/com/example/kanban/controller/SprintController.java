package com.example.kanban.controller;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
        return  ResponseEntity.ok(sprintService.createSprint(sprintDTO));
    }
}
