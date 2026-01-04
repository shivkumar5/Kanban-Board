package com.example.kanban.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Setter @Getter
public class TaskDTO {
    private UUID id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private UUID sprintId;
    private UUID statusId;
    private  UUID userId;
}
