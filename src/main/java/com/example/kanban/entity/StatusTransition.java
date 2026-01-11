package com.example.kanban.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)  
@EqualsAndHashCode
@Table(name = "status_transitions")
public class StatusTransition {
    @EmbeddedId
    private StatusTransitionId id;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}


