package com.example.kanban.controller;

import com.example.kanban.dto.UserDTO;
import com.example.kanban.entity.User;
import com.example.kanban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser((userDTO)));
    }

    @GetMapping
    public List<User> getUser(){
        logger.info("GET request received for all users!"); // This will show in IntelliJ
        return userService.getAllUsers();
    }

    @GetMapping("/ping")
    public String ping() {
        logger.info("--- THE PROGRAM REACHED THE CONTROLLER ---");
        return "Pong!";
    }
}
