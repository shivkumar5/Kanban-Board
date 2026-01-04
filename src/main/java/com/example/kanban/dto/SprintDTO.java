package com.example.kanban.dto;

import com.example.kanban.entity.User;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SprintDTO {
    private UUID id;
    private String name;
    private Date startDate;
    private Date endDate;
    private UUID userId;
}
