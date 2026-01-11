package com.example.kanban.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class StatusTransitionId implements Serializable{
    private UUID sourceStatusId;
    private UUID targetStatusId;

}
