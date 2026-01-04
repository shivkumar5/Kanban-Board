package com.example.kanban.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class StatusDTO {
    private UUID id;
    private String name;
}
