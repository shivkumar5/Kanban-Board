package com.example.kanban.controller;

import com.example.kanban.dto.UserDTO;
import com.example.kanban.entity.User;
import com.example.kanban.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users Management", description = "APIs for managing users in the Kanban system")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the Kanban system. Email must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already exists or invalid request data",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User details including name and email", required = true)
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @Operation(
            summary = "List users with pagination and filters",
            description = "Retrieves a paginated list of users with optional filtering by name and email. " +
                    "Results are sorted by createdAt in descending order by default."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @Parameter(description = "Filter users by name (partial match, case-insensitive)", example = "John")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter users by email (partial match, case-insensitive)", example = "john@example.com")
            @RequestParam(required = false) String email,
            @Parameter(description = "Pagination parameters (page, size, sort). Default: page=0, size=20, sort=createdAt,DESC")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.info("GET request received for users with filters: name={}, email={}", name, email);
        return ResponseEntity.ok(userService.getUsers(name, email, pageable));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(
            summary = "Update user (full replace)",
            description = "Performs a full replacement of an existing user. All fields in the request body will replace the existing user data."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
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
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID userId,
            @Parameter(description = "Complete user data to replace existing user", required = true)
            @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @Operation(
            summary = "Partially update user",
            description = "Updates only the specified fields of a user. Only include the fields you want to update. " +
                    "Supported fields: name, email"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = User.class))
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
    @PatchMapping("/{userId}")
    public ResponseEntity<User> patchUser(
            @Parameter(description = "User ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID userId,
            @Parameter(description = "Map of fields to update (e.g., {\"name\": \"New Name\", \"email\": \"new@email.com\"})", required = true)
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(userService.patchUser(userId, updates));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user from the system. Note: This may fail if there are tasks or sprints associated with this user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete user (may be in use by tasks or sprints)",
                    content = @Content
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
