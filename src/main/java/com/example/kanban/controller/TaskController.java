package com.example.kanban.controller;

import com.example.kanban.dto.TaskDTO;
import com.example.kanban.entity.Task;
import com.example.kanban.service.TaskService;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDTO));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Task> patchTask(@PathVariable Long id, @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(taskService.patchTask(id, patch));
    }

}
