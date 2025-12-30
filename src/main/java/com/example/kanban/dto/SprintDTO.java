package com.example.kanban.dto;

import com.example.kanban.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class SprintDTO {
    private String name;
    private Date startDate;
    private Date endDate;
    private Long userId;
}
