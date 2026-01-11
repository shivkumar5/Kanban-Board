package com.example.kanban.controller;

import com.example.kanban.dto.UserDTO;
import com.example.kanban.entity.User;
import com.example.kanban.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name ="Users Management", description = "APIs for managing users in the Kanban system")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user in the Kanban system")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser((userDTO)));
    }

    @Operation(summary = "List users", description = "Retrieve all users")
    @GetMapping
    public List<User> getUser(){
        logger.info("GET request received for all users!"); // This will show in IntelliJ
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user", description = "Retrieve a single user by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable java.util.UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Update user", description = "Replace user data by ID")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable java.util.UUID userId, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @Operation(summary = "Patch user", description = "Partially update a user's fields")
    @PatchMapping("/{userId}")
    public ResponseEntity<User> patchUser(@PathVariable java.util.UUID userId, @RequestBody java.util.Map<String, Object> updates) {
        return ResponseEntity.ok(userService.patchUser(userId, updates));
    }
}
