package com.example.kanban.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter @Getter
public class TaskDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long sprintId;
    private Long statusId;
    private  Long userId;
}
