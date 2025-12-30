package com.example.kanban.controller;

import com.example.kanban.dto.StatusDTO;
import com.example.kanban.entity.Status;
import com.example.kanban.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    @Autowired
    StatusService statusService;

    @PostMapping
    ResponseEntity<Status> createStatus(@RequestBody StatusDTO statusDTO) {
        return  ResponseEntity.ok(statusService.createStatus(statusDTO));
    }

    @GetMapping
    ResponseEntity<List<Status>> getAllStatus() {
        return  ResponseEntity.ok(statusService.getAllStatus());
    }

}
